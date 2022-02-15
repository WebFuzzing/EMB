package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.database.tinkerpop.IndexHandler;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.Neo4jLuceneEntityFetcher;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class Neo4jLuceneEntityFetcherTest {

  private IndexHandler indexHandler;

  @Before
  public void setUp() throws Exception {
    indexHandler = mock(IndexHandler.class);
  }

  @Test
  public void getEntityRetrievesTheLatestFromTheGraphEvenIfTheIndexIsNotUpToDate() {
    final UUID timId = UUID.randomUUID();
    final String collectionName = "things";
    TinkerPopGraphManager graphManager = newGraph()
      .withVertex("latest", v -> v
        .withTimId(timId)
        .isLatest(true))
      .withVertex(v -> v
        .withTimId(timId)
        .isLatest(false)
        .withOutgoingRelation("VERSION_OF", "latest")
      )
      .wrap();
    GraphTraversal<Vertex, Vertex> secondLatestVertexT = graphManager.getGraph()
                                                                     .traversal().V()
                                                                     .has("tim_id", timId.toString())
                                                                     .has("isLatest", false);
    given(indexHandler.findById(timId)).willReturn(Optional.of(secondLatestVertexT.next()));
    Neo4jLuceneEntityFetcher instance = new Neo4jLuceneEntityFetcher(graphManager, indexHandler);
    GraphTraversalSource traversal = graphManager.getGraph().traversal();

    GraphTraversal<Vertex, Vertex> entityT = instance.getEntity(traversal, timId, null, collectionName);

    assertThat(entityT.hasNext(), is(true));
    assertThat(entityT.next(), is(likeVertex().withTimId(timId).withProperty("isLatest", true)));
  }

  @Test
  public void getEntityRetrievesTheVertexDirectFromTheDatabaseWhenTheIndexDoesNotContainIt() {
    final UUID timId = UUID.randomUUID();
    final String things = "things";
    TinkerPopGraphManager graphManager = newGraph()
      .withVertex("latest", v -> v
        .withTimId(timId)
        .isLatest(true))
      .withVertex(v -> v
        .withTimId(timId)
        .isLatest(false)
        .withOutgoingRelation("VERSION_OF", "latest")
      )
      .wrap();
    given(indexHandler.findById(timId)).willReturn(Optional.empty());
    Neo4jLuceneEntityFetcher instance = new Neo4jLuceneEntityFetcher(graphManager, indexHandler);
    GraphTraversalSource traversal = graphManager.getGraph().traversal();

    GraphTraversal<Vertex, Vertex> entityT = instance.getEntity(traversal, timId, null, things);

    assertThat(entityT.next().value("tim_id"), equalTo(timId.toString()));
  }
}
