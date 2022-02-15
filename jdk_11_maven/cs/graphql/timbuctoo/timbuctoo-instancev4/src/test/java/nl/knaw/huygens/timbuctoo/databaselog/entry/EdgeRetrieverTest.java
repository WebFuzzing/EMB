package nl.knaw.huygens.timbuctoo.databaselog.entry;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.Test;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.EdgeMatcher.likeEdge;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EdgeRetrieverTest {
  @Test
  public void getPreviousVersionReturnsThePreviousVersion() {
    UUID edgeId = UUID.randomUUID();
    Graph graph = newGraph().withVertex("v1", v -> v.withTimId("id1"))
                            .withVertex("v2",
                              v -> v.withOutgoingRelation("edge", "v1", e -> e.withRev(1).withTim_id(edgeId))
                                    .withOutgoingRelation("edge", "v1", e -> e.withRev(2).withTim_id(edgeId)))
                            .build();
    Edge edge = graph.traversal().E().has("tim_id", edgeId.toString()).has("rev", 2).next();
    EdgeRetriever instance = new EdgeRetriever();

    Edge previousVersion = instance.getPreviousVersion(edge);

    assertThat(previousVersion, is(likeEdge().withId(edgeId.toString()).withProperty("rev", 1)));
  }
}
