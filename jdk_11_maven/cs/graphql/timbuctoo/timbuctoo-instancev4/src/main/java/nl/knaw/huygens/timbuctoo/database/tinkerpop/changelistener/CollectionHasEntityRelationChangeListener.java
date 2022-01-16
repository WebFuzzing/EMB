package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;

import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;

public class CollectionHasEntityRelationChangeListener implements ChangeListener {
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(CollectionHasEntityRelationChangeListener.class);

  private final GraphWrapper graphWrapper;

  public CollectionHasEntityRelationChangeListener(GraphWrapper graphWrapper) {
    this.graphWrapper = graphWrapper;
  }

  @Override
  public void onCreate(Collection collection, Vertex vertex) {
  }

  @Override
  public void onPropertyUpdate(Collection collection, Optional<Vertex> ignored, Vertex vertexToUpdate) {
  }

  @Override
  public void onRemoveFromCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    String type = collection.getEntityTypeName();
    final GraphTraversal<Vertex, Vertex> hasEntityNode = findEntityNodeForEntityTypeName(type);
    final GraphTraversal<Vertex, Edge> edgeToRemove = hasEntityNode.outE(Collection.HAS_ENTITY_RELATION_NAME)
      .where(__.inV().is(newVertex));
    if (edgeToRemove.hasNext()) {
      edgeToRemove.next().remove();
    }
  }

  @Override
  public void onAddToCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    String type = collection.getEntityTypeName();
    final GraphTraversal<Vertex, Vertex> hasEntityNode = findEntityNodeForEntityTypeName(type);

    if (hasEntityNode.hasNext()) {
      hasEntityNode.next().addEdge(Collection.HAS_ENTITY_RELATION_NAME, newVertex);
    } else {
      LOG.error(databaseInvariant, "No hasEntity node found for collection with entityTypeName {} ", type);
    }
  }

  @Override
  public void onCreateEdge(Collection collection, Edge edge) {

  }

  @Override
  public void onEdgeUpdate(Collection collection, Edge oldEdge, Edge newEdge) {

  }

  private GraphTraversal<Vertex, Vertex> findEntityNodeForEntityTypeName(String type) {
    return graphWrapper
      .getGraph().traversal().V()
      .hasLabel(Collection.DATABASE_LABEL)
      .has(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, type)
      .out(Collection.HAS_ENTITY_NODE_RELATION_NAME);
  }
}
