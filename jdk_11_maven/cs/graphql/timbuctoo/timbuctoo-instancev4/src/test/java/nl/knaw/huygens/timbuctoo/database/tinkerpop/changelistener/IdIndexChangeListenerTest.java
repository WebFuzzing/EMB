package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.IndexHandler;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class IdIndexChangeListenerTest {

  public static final Collection NULL_COLLECTION = null;
  private IndexHandler indexHandler;
  private IdIndexChangeListener instance;

  @Before
  public void setUp() throws Exception {
    indexHandler = mock(IndexHandler.class);
    instance = new IdIndexChangeListener(indexHandler);
  }

  @Test
  public void onCreateAddsTheVertexToTheIdIndex() {
    UUID timId = UUID.randomUUID();
    Vertex vertex = vertexWithId(timId);
    IndexHandler indexHandler = mock(IndexHandler.class);
    IdIndexChangeListener instance = new IdIndexChangeListener(indexHandler);

    instance.onCreate(NULL_COLLECTION, vertex);

    verify(indexHandler).upsertIntoIdIndex(timId, vertex);
  }

  private Vertex vertexWithId(UUID timId) {
    Graph graph = newGraph().withVertex(v -> v.withTimId(timId)).build();
    return graph.traversal().V().has("tim_id", timId.toString()).next();
  }

  @Test
  public void onPropertyUpdateRemovesTheOldVertexFromTheIdIndexBeforeAddingTheNewOne() {
    UUID timId = UUID.randomUUID();
    Vertex oldVertex = vertexWithId(timId);
    Vertex newVertex = vertexWithId(timId);

    instance.onPropertyUpdate(NULL_COLLECTION, Optional.of(oldVertex), newVertex);

    verify(indexHandler).upsertIntoIdIndex(timId, newVertex);
  }

  @Test
  public void onPropertyUpdateDoesNotRemoveTheOldVertexWhenTheOptionalIsEmpty() {
    UUID timId = UUID.randomUUID();
    Vertex newVertex = vertexWithId(timId);

    instance.onPropertyUpdate(NULL_COLLECTION, Optional.empty(), newVertex);

    verify(indexHandler, never()).removeFromIdIndex(any(Vertex.class));
  }

  @Test
  public void onRemoveFromCollectionDoesNothing() {
    Vertex nullVertex = null;

    instance.onRemoveFromCollection(NULL_COLLECTION, Optional.empty(), nullVertex);

    verifyNoInteractions(indexHandler);
  }

  @Test
  public void onAddToCollectionDoesNothing() {
    Vertex nullVertex = null;

    instance.onAddToCollection(NULL_COLLECTION, Optional.empty(), nullVertex);

    verifyNoInteractions(indexHandler);
  }

  @Test
  public void onCreateEdgeCallTheIndexHandler() {
    UUID timId = UUID.randomUUID();
    Edge edge = edgeWithId(timId);

    instance.onCreateEdge(NULL_COLLECTION, edge);

    verify(indexHandler).upsertIntoEdgeIdIndex(timId, edge);
  }

  @Test
  public void onEdgeUpdateCallsTheIndexHandler() {
    UUID timId = UUID.randomUUID();
    Edge edge = edgeWithId(timId);
    Edge nullEdge = null;

    instance.onEdgeUpdate(NULL_COLLECTION, nullEdge, edge);

    verify(indexHandler).upsertIntoEdgeIdIndex(timId, edge);
  }

  private Edge edgeWithId(UUID timId) {
    Graph graph = newGraph()
      .withVertex(v -> v.withOutgoingRelation("rel", "other", e -> e.withTim_id(timId)))
      .withVertex("other", v -> {
      })
      .build();
    return graph.traversal().E().has("tim_id", timId.toString()).next();
  }


}
