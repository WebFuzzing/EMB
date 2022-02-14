package nl.knaw.huygens.timbuctoo.server.databasemigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The indices currently point to the last vertex *before* the one with isLatest and all the relations.
 * With the new code they will point to the vertex that *is* the latest one.
 * This migration regenerates the affected vertices
 */
public class MoveIndicesToIsLatestVertexMigration implements DatabaseMigration {

  private static final Logger LOG = getLogger(MoveIndicesToIsLatestVertexMigration.class);
  private final Vres vres;

  public MoveIndicesToIsLatestVertexMigration(Vres vres) {
    this.vres = vres;
  }

  @Override
  public void execute(TinkerPopGraphManager graphWrapper) throws IOException {

    GraphDatabaseService service = graphWrapper.getGraphDatabase();
    IndexManager indexManager = service.index();

    try (Transaction tx =  graphWrapper.getGraph().tx()) {
      clearIndices(indexManager);
      tx.commit();
    }
    try (Transaction tx =  graphWrapper.getGraph().tx()) {
      if (!tx.isOpen()) {
        tx.open();
      }
      constructIndices(indexManager);
      fillVertexIndices(graphWrapper, indexManager);
      fillEdgeIndex(service, indexManager);
      tx.commit();
    }
  }

  private void constructIndices(IndexManager indexManager) {
    Map<String, String> indexConfig = MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext");

    //construct all quicksearchIndices
    vres.getVres().values().stream()
      .flatMap(vre -> vre.getCollections().values().stream())
      .forEach((collection) -> {
        indexManager.forNodes(collection.getCollectionName(), indexConfig);
      });

    //construct id index
    indexManager.forNodes("idIndex");
    //construct edge index
    indexManager.forNodes("edgeIdIndex");
  }

  private void clearIndices(IndexManager indexManager) {
    //delete all quicksearchIndices
    vres.getVres().values().stream()
        .flatMap(vre -> vre.getCollections().values().stream())
        .forEach((collection) -> {
          deleteIndex(indexManager, collection.getCollectionName());
        });

    //delete id index
    deleteIndex(indexManager, "idIndex");
    //delete edge index
    deleteIndex(indexManager, "edgeIdIndex");
  }

  private void fillVertexIndices(TinkerPopGraphManager graphWrapper, IndexManager indexManager) {
    ObjectMapper mapper = new ObjectMapper();
    GraphTraversalSource traversalSource = graphWrapper.getGraph().traversal();

    Collection wwpersonCollection = vres.getCollection("wwpersons").orElse(null);

    graphWrapper.getGraphDatabase().getAllNodes().stream()
      .filter(node -> node.hasProperty("tim_id"))
      .filter(this::isLatest)
      .forEach(node -> {
        updateIdIndex(indexManager, node);
        if (!isDeleted(node)) {
          updateQuicksearchIndex(vres, indexManager, mapper, node, wwpersonCollection, traversalSource);
        }
      });
  }

  private void fillEdgeIndex(GraphDatabaseService service, IndexManager indexManager) {
    service.getAllRelationships().stream()
      .filter(e -> e.hasProperty("tim_id"))
      .filter(this::isLatest)
      .forEach(edge -> {
        indexManager.forRelationships("edgeIdIndex").add(edge, "tim_id", edge.getProperty("tim_id"));
      });
  }

  private void updateIdIndex(IndexManager indexManager, Node node) {
    indexManager.forNodes("idIndex").add(node, "tim_id", node.getProperty("tim_id"));
  }

  private void updateQuicksearchIndex(Vres vres, IndexManager indexManager, ObjectMapper mapper, Node node,
                                      Collection wwpersonCollection, GraphTraversalSource traversalSource) {
    for (String type : getEntityTypes(node, mapper)) {
      if (!type.equals("relationtype")) {
        Optional<Collection> collection = vres.getCollectionForType(type);
        long id = node.getId();
        if (collection.isPresent()) {
          String displayName;
          if (type.equals("wwdocument")) {
            displayName = getWwDocumentsQuickSearchValue(collection.get(), wwpersonCollection,
              id, traversalSource
            );
          } else {
            displayName = getGenericQuickSearchValue(collection.get(), id, traversalSource);
          }
          indexManager.forNodes(collection.get().getCollectionName()).add(node, "quickSearch", displayName);
        } else {
          LOG.error("Could not find collection for " + type + " at vertex " + id);
        }
      }
    }
  }

  private void deleteIndex(IndexManager indexManager, String name) {
    if (indexManager.existsForNodes(name)) {
      indexManager.forNodes(name).delete();
    }
  }

  private String[] getEntityTypes(Node node, ObjectMapper mapper) {
    try {
      Object typesProp = node.getProperty("types");
      if (!(typesProp instanceof String) || typesProp.equals("") || typesProp.equals("[]")) {
        LOG.error(databaseInvariant, "Types property is empty at vertex with ID " + node.getId());
        return new String[0];
      } else {
        return mapper.readValue((String) typesProp, String[].class);
      }
    } catch (IOException e) {
      LOG.error(databaseInvariant, "Could not parse types property at vertex with ID " + node.getId());
      return new String[0];
    } catch (NotFoundException e) {
      LOG.error(databaseInvariant, "No types property found at vertex with ID " + node.getId());
      return new String[0];
    }
  }

  private boolean isLatest(Entity node) {
    return getBoolean(node, "isLatest");
  }

  private boolean isDeleted(Entity node) {
    return getBoolean(node, "isDeleted");
  }

  private boolean getBoolean(Entity node, String name) {
    if (!node.hasProperty(name)) {
      return false;
    }
    Object isLatest = node.getProperty(name);
    if (isLatest instanceof Boolean) {
      return (boolean) isLatest;
    } else {
      LOG.error(name + " property that is not a boolean on Vertex " + node.getId());
      return false;
    }
  }

  private String getWwDocumentsQuickSearchValue(Collection wwDocumentsCollection, Collection wwPersonsCollection,
                                                long nodeId, GraphTraversalSource traversalS) {
    String docCaption = getGenericQuickSearchValue(wwDocumentsCollection, nodeId, traversalS);

    String authors = traversalS.V(nodeId)
      .outE("isCreatedBy").has("wwrelation_accepted", true).has("isLatest", true).otherV()
      .toStream()
      .map(v -> getGenericQuickSearchValue(wwPersonsCollection, (Long) v.id(), traversalS))
      .sorted()
      .collect(Collectors.joining(" "));
    return authors + " " + docCaption;
  }

  private String getGenericQuickSearchValue(Collection collection, long nodeId, GraphTraversalSource traversalS) {
    return traversalS.V(nodeId)
      .union(collection.getDisplayName().traversalJson())
      .map(nodeOrException -> unwrapError(nodeId, nodeOrException))
      .toStream().findAny()
      .orElseGet(() -> {
        LOG.debug("Displayname traversal resulted in no results vertexId={} collection={} propertyType={}",
          nodeId, collection.getCollectionName(), collection.getDisplayName().getUniqueTypeId());
        return "";//this value is not displayed anywhere, just used for search
      });
  }

  private String unwrapError(long nodeId, Traverser<Try<JsonNode>> nodeOrException) {
    return nodeOrException.get().getOrElseGet(e -> {
      LOG.error("An error occurred while generating the displayName for " + nodeId);
      return jsn("");//this value is not displayed anywhere, just used for search
    }).asText("");
  }
}


