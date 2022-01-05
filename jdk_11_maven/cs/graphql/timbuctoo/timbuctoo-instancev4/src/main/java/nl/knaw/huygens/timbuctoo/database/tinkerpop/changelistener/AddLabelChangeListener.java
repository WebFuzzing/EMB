package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class AddLabelChangeListener implements ChangeListener {
  private static final Logger LOG = getLogger(AddLabelChangeListener.class);

  @Override
  public void onCreate(Collection collection, Vertex vertex) {
  }

  @Override
  public void onPropertyUpdate(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
  }

  @Override
  public void onRemoveFromCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    ((Neo4jVertex) newVertex).removeLabel(collection.getEntityTypeName());
  }

  @Override
  public void onAddToCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    ((Neo4jVertex) newVertex).addLabel(collection.getEntityTypeName());
  }

  @Override
  public void onCreateEdge(Collection collection, Edge edge) {

  }

  @Override
  public void onEdgeUpdate(Collection collection, Edge oldEdge, Edge newEdge) {

  }

  public void handleRdfLabelAdd(Vertex vertex, String entityTypeName) {
    ((Neo4jVertex) vertex).addLabel(entityTypeName);
  }

  public void handleRdfLabelRemove(Vertex vertex, String entityTypeName) {
    ((Neo4jVertex) vertex).removeLabel(entityTypeName);
  }

}
