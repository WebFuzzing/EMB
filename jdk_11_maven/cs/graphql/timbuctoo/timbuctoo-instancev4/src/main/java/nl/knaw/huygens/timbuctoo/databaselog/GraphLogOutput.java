package nl.knaw.huygens.timbuctoo.databaselog;

import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GraphLogOutput implements LogOutput {
  public static final Logger LOG = LoggerFactory.getLogger(GraphLogOutput.class);
  private final GraphWrapper graphWrapper;
  private Vertex currentVertex;
  private Transaction tx;
  private int numberOfEntriesWithoutCommit;

  public GraphLogOutput(GraphWrapper graphWrapper) {
    this.graphWrapper = graphWrapper;
    this.numberOfEntriesWithoutCommit = 0;
  }

  @Override
  public void prepareToWrite() {
    LOG.info("Prepare to write");
    tx = graphWrapper.getGraph().tx();
    if (!tx.isOpen()) {
      tx.open();
    }
    currentVertex = graphWrapper.getGraph().addVertex("history");
    commit();
  }

  @Override
  public void newVertex(Vertex vertex) {
    LOG.debug("New vertex {}", vertex.id());
    Vertex previousVertex = currentVertex;
    currentVertex = graphWrapper.getGraph().addVertex("createVertexEntry");
    previousVertex.addEdge("NEXT_ITEM", currentVertex);
    currentVertex.property("TIM_tim_id", vertex.value("tim_id"));
    commit();
  }

  @Override
  public void updateVertex(Vertex vertex) {
    LOG.debug("Update vertex {}", vertex.id());
    Vertex previousVertex = currentVertex;
    currentVertex = graphWrapper.getGraph().addVertex("updateVertexEntry");
    previousVertex.addEdge("NEXT_ITEM", currentVertex);
    currentVertex.property("TIM_tim_id", vertex.value("tim_id"));
    commit();
  }

  @Override
  public void newEdge(Edge edge) {
    LOG.debug("New edge {}", edge.id());
    Vertex previousVertex = currentVertex;
    currentVertex = graphWrapper.getGraph().addVertex("createEdgeEntry");
    previousVertex.addEdge("NEXT_ITEM", currentVertex);
    currentVertex.property("TIM_inVertex", edge.inVertex().value("tim_id"));
    currentVertex.property("TIM_outVertex", edge.outVertex().value("tim_id"));
    currentVertex.property("TIM_tim_id", edge.value("tim_id"));
    commit();
  }

  @Override
  public void updateEdge(Edge edge) {
    LOG.debug("Update edge {}", edge.id());
    Vertex previousVertex = currentVertex;
    currentVertex = graphWrapper.getGraph().addVertex("updateEdgeEntry");
    previousVertex.addEdge("NEXT_ITEM", currentVertex);
    currentVertex.property("TIM_inVertex", edge.inVertex().value("tim_id"));
    currentVertex.property("TIM_outVertex", edge.outVertex().value("tim_id"));
    currentVertex.property("TIM_tim_id", edge.value("tim_id"));
    commit();
  }

  private void commit() {
    if (++numberOfEntriesWithoutCommit >= 5000) {
      numberOfEntriesWithoutCommit = 0;
      tx.commit();
      tx.close();
      tx.open();
    }
  }

  @Override
  public void newProperty(Property property) {
    LOG.debug("New property {} {}", property.key(), property.value());
    currentVertex.property(property.key(), property.value());
  }

  @Override
  public void updateProperty(Property property) {
    LOG.debug("Update property {} {}", property.key(), property.value());
    currentVertex.property(property.key(), property.value());
  }

  @Override
  public void deleteProperty(String propertyName) {
    LOG.debug("Delete property {} of vertex with id {}", propertyName, currentVertex.id());
    currentVertex.property(propertyName, "!DELETED!");
  }

  @Override
  public void finishWriting() {
    tx.commit();
    tx.close();
    LOG.info("Finish writing");
  }
}
