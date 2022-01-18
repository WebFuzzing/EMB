package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.IndexHandler;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Optional;
import java.util.UUID;

public class IdIndexChangeListener implements ChangeListener {
  private final IndexHandler indexHandler;

  public IdIndexChangeListener(IndexHandler indexHandler) {
    this.indexHandler = indexHandler;
  }

  @Override
  public void onCreate(Collection collection, Vertex vertex) {
    indexHandler.upsertIntoIdIndex(UUID.fromString(vertex.value("tim_id")), vertex);
  }

  @Override
  public void onPropertyUpdate(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
    indexHandler.upsertIntoIdIndex(UUID.fromString(newVertex.value("tim_id")), newVertex);
  }

  @Override
  public void onRemoveFromCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {

  }

  @Override
  public void onAddToCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {

  }

  @Override
  public void onCreateEdge(Collection collection, Edge edge) {
    indexHandler.upsertIntoEdgeIdIndex(UUID.fromString(edge.value("tim_id")), edge);
  }

  @Override
  public void onEdgeUpdate(Collection collection, Edge oldEdge, Edge newEdge) {
    indexHandler.upsertIntoEdgeIdIndex(UUID.fromString(newEdge.value("tim_id")), newEdge);
  }
}
