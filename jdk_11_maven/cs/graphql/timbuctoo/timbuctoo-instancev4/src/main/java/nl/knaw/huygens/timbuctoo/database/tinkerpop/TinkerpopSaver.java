package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescription;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescriptions;
import nl.knaw.huygens.timbuctoo.bulkupload.savers.Saver;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

public class TinkerpopSaver implements AutoCloseable, Saver<Vertex> {
  private static final Logger LOG = getLogger(TinkerpopSaver.class);
  public static final String RAW_COLLECTION_EDGE_NAME = "hasRawCollection";
  public static final String RAW_ITEM_EDGE_NAME = "hasItem";
  public static final String RAW_COLLECTION_NAME_PROPERTY_NAME = "name";
  public static final String NEXT_RAW_ITEM_EDGE_NAME = "hasNextItem";
  public static final String RAW_PROPERTY_EDGE_NAME = "hasProperty";
  public static final String FIRST_RAW_PROPERTY_EDGE_NAME = "hasFirstProperty";
  public static final String NEXT_RAW_PROPERTY_EDGE_NAME = "hasNextProperty";
  public static final String RAW_PROPERTY_NAME = "name";
  public static final String VALUE_PREFIX = "value:";
  public static final String ERROR_PREFIX = "error:";
  public static final String SAVED_MAPPING_STATE = "savedMappingState";
  private final GraphWrapper graphWrapper;
  private final Vertex vre;
  private final int maxVerticesPerTransaction;
  private final VreIniter vreIniter;
  private int saveCounter;
  private Transaction tx;
  private Vertex curCollection;
  private Vertex lastVertex;

  public TinkerpopSaver(Vres vres, GraphWrapper graphWrapper, String vreName, String vreLabel,
                        int maxVerticesPerTransaction, String fileName) {
    this.graphWrapper = graphWrapper;
    tx = graphWrapper.getGraph().tx();
    this.maxVerticesPerTransaction = maxVerticesPerTransaction;
    this.vreIniter = new VreIniter(graphWrapper, vres);
    this.vre = vreIniter.upsertVre(vreName, vreLabel, fileName);
  }

  private void allowCommit() {
    if (saveCounter++ > maxVerticesPerTransaction) {
      saveCounter = 0;
      tx.commit();
      tx = graphWrapper.getGraph().tx();
    }
  }

  @Override
  public void close() {
    tx.commit();
  }

  @Override
  public Vertex addEntity(Vertex rawCollection, Map<String, String> currentProperties) {
    allowCommit();

    Vertex result = graphWrapper.getGraph().addVertex("tim_id", UUID.randomUUID().toString());
    currentProperties.forEach((key, val) -> result.property(VALUE_PREFIX + key, val));

    addToLastItemOfCollection(rawCollection, result);
    addToCollection(rawCollection, result);

    return result;
  }

  @Override
  public Vertex addCollection(String collectionName) {
    Vertex collection = graphWrapper.getGraph().addVertex(RAW_COLLECTION_NAME_PROPERTY_NAME, collectionName);
    vre.addEdge(RAW_COLLECTION_EDGE_NAME, collection);
    return collection;
  }

  @Override
  public void addPropertyDescriptions(Vertex collection, ImportPropertyDescriptions importPropertyDescriptions) {
    Graph graph = graphWrapper.getGraph();
    Vertex previousPropertyDesc = null;
    for (ImportPropertyDescription importPropertyDescription : importPropertyDescriptions) {
      Vertex propertyDesc = graph.addVertex();
      propertyDesc.property("id", importPropertyDescription.getId());
      propertyDesc.property(RAW_PROPERTY_NAME, importPropertyDescription.getPropertyName());
      propertyDesc.property("order", importPropertyDescription.getOrder());

      collection.addEdge(RAW_PROPERTY_EDGE_NAME, propertyDesc);

      if (previousPropertyDesc == null) {
        collection.addEdge(FIRST_RAW_PROPERTY_EDGE_NAME, propertyDesc);
      } else {
        previousPropertyDesc.addEdge(NEXT_RAW_PROPERTY_EDGE_NAME, propertyDesc);
      }

      previousPropertyDesc = propertyDesc;
    }
  }

  public void setUploadFinished(String vreName, Vre.PublishState publishState) {
    try (Transaction tx = graphWrapper.getGraph().tx()) {

      final GraphTraversal<Vertex, Vertex> vreT = vreIniter.getVreTraversal(vreName);
      if (vreT.hasNext()) {
        vreT.next().property(Vre.PUBLISH_STATE_PROPERTY_NAME, publishState.toString());
      }
      tx.commit();
    }
  }

  public void addToLastItemOfCollection(Vertex rawCollection, Vertex result) {
    if (curCollection == rawCollection && lastVertex != null) {
      //should be the one that usually called
      lastVertex.addEdge(TinkerpopSaver.NEXT_RAW_ITEM_EDGE_NAME, result);
      lastVertex = result;
    } else if (!rawCollection.edges(Direction.OUT, TinkerpopSaver.NEXT_RAW_ITEM_EDGE_NAME).hasNext()) {
      //should be called only once
      rawCollection.addEdge(TinkerpopSaver.NEXT_RAW_ITEM_EDGE_NAME, result);
      lastVertex = result;
      curCollection = rawCollection;
    } else {
      //should never be called
      LOG.error("This method is slow and ought to never be called.");
      Vertex previous = graphWrapper.getGraph().traversal().V(rawCollection.id())
        .out(TinkerpopSaver.RAW_ITEM_EDGE_NAME)
        .not(__.where(__.out(TinkerpopSaver.NEXT_RAW_ITEM_EDGE_NAME)))
        .next();

      previous.addEdge(TinkerpopSaver.NEXT_RAW_ITEM_EDGE_NAME, result);
    }
  }

  public void addToCollection(Vertex rawCollection, Vertex result) {
    rawCollection.addEdge(TinkerpopSaver.RAW_ITEM_EDGE_NAME, result);
  }

}
