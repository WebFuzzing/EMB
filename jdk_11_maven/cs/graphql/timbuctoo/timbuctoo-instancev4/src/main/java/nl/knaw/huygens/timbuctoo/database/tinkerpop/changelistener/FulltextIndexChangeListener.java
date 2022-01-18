package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;


import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.IndexHandler;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;

public class FulltextIndexChangeListener implements ChangeListener {
  private static final Logger LOG = LoggerFactory.getLogger(FulltextIndexChangeListener.class);

  private final IndexHandler indexHandler;
  private final GraphWrapper graphWrapper;

  public FulltextIndexChangeListener(IndexHandler indexHandler, GraphWrapper graphWrapper) {
    this.indexHandler = indexHandler;
    this.graphWrapper = graphWrapper;
  }

  @Override
  public void onCreate(Collection collection, Vertex vertex) {
    handleChange(collection, vertex, null);
  }

  @Override
  public void onPropertyUpdate(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    handleChange(collection, newVertex, oldVertex.orElse(null));
  }

  @Override
  public void onRemoveFromCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    oldVertex.ifPresent(vertex -> handleRemove(collection, vertex));
    handleRemove(collection, newVertex);
  }

  @Override
  public void onAddToCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    handleChange(collection, newVertex, oldVertex.orElse(null));
  }

  @Override
  public void onCreateEdge(Collection collection, Edge edge) {
    handleRelationChange(collection, edge);
  }

  @Override
  public void onEdgeUpdate(Collection collection, Edge oldEdge, Edge newEdge) {
    handleRelationChange(collection, newEdge);
  }

  private void handleRelationChange(Collection collection, Edge edge) {
    if ("isCreatedBy".equals(edge.label()) && "wwrelation".equals(collection.getEntityTypeName())) {
      Vertex document = edge.outVertex();
      Vertex oldDocument = document.vertices(Direction.IN, "VERSION_OF").next();
      Collection wwdocuments = collection.getVre().getCollectionForTypeName("wwdocument");
      handleChange(wwdocuments, document, oldDocument);
    }
  }

  private void handleChange(Collection collection, Vertex vertex, Vertex oldVertex) {
    long vertexId = (long) vertex.id();
    GraphTraversalSource traversalSource = graphWrapper.getGraph().traversal();

    if (collection.getEntityTypeName().equals("wwdocument")) {
      final String displayName;
      Collection wwPersonsCollection = collection.getVre().getCollectionForTypeName("wwperson");
      displayName = getWwDocumentsQuickSearchValue(collection, wwPersonsCollection, vertexId, traversalSource);
      indexHandler.upsertIntoQuickSearchIndex(collection, displayName, vertex, oldVertex);
    } else if (collection.getEntityTypeName().equals("wwperson")) {
      Collection wwDocumentCollection = collection.getVre().getCollectionForTypeName("wwdocument");
      vertex.vertices(Direction.OUT, "isCreatedBy").forEachRemaining(doc -> {
        final String displayName;
        displayName = getWwDocumentsQuickSearchValue(wwDocumentCollection, collection, vertexId, traversalSource);
        indexHandler.upsertIntoQuickSearchIndex(collection, displayName, vertex, oldVertex);
      });
      String displayName = getGenericQuickSearchValue(collection, vertexId, traversalSource);
      indexHandler.upsertIntoQuickSearchIndex(collection, displayName, vertex, oldVertex);
    } else {
      final String displayName;
      displayName = getGenericQuickSearchValue(collection, vertexId, traversalSource);
      indexHandler.upsertIntoQuickSearchIndex(collection, displayName, vertex, oldVertex);
    }

  }

  private String getWwDocumentsQuickSearchValue(Collection wwDocumentsCollection, Collection wwPersonsCollection,
                                                long nodeId, GraphTraversalSource traversalS) {
    String docCaption = getGenericQuickSearchValue(wwDocumentsCollection, nodeId, traversalS);

    String authors = traversalS.V(nodeId)
      .outE("isCreatedBy").has("wwrelation_accepted", true).has("isLatest", true).otherV()
      .toStream()
      .map(v -> getGenericQuickSearchValue(wwPersonsCollection, (long) v.id(), traversalS))
      .sorted()

      .collect(Collectors.joining("; "));
    return authors + " " + docCaption;
  }

  private String getGenericQuickSearchValue(Collection collection, long nodeId, GraphTraversalSource traversalS) {
    return traversalS.V(nodeId)
      .union(collection.getDisplayName().traversalJson())
      .map(nodeOrException -> unwrapError(nodeId, nodeOrException))
      .toStream().findAny()
      .orElseGet(() -> {
        LOG.warn(databaseInvariant,
          "Displayname traversal resulted in no results vertexId={} collection={} propertyType={}",
          nodeId, collection.getCollectionName(), collection.getDisplayName().getUniqueTypeId());
        return "(empty)";
      });
  }

  private String unwrapError(long nodeId, Traverser<Try<JsonNode>> nodeOrException) {
    return nodeOrException.get().getOrElseGet(e -> {
      LOG.error("An error occurred while generating the displayName for " + nodeId);
      return jsn("#Error#");
    }).asText("");
  }

  private void handleRemove(Collection collection, Vertex vertex) {
    indexHandler.removeFromQuickSearchIndex(collection, vertex);
  }
}
