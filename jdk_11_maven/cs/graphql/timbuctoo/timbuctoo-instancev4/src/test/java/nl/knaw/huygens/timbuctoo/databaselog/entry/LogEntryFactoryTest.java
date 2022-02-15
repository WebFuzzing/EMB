package nl.knaw.huygens.timbuctoo.databaselog.entry;

import nl.knaw.huygens.timbuctoo.databaselog.LogEntry;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static nl.knaw.huygens.timbuctoo.util.EdgeMockBuilder.edge;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class LogEntryFactoryTest {
  @Test
  public void createForVertexCreatesACreateVertexLogEntry() {
    Vertex vertex = vertex().build();
    LogEntryFactory instance = new LogEntryFactory();

    LogEntry entry = instance.createForVertex(vertex);

    assertThat(entry, is(instanceOf(CreateVertexLogEntry.class)));
  }

  @Test
  public void createForVertexCreatesAnUpdateVertexLogEntryIfTheVertexHasIncomingVersionOfEdges() {
    Vertex vertex = vertex().withIncomingRelation("VERSION_OF", vertex().build()).build();
    LogEntryFactory instance = new LogEntryFactory();

    LogEntry entry = instance.createForVertex(vertex);

    assertThat(entry, is(instanceOf(UpdateVertexLogEntry.class)));
  }

  @Test
  public void createForEdgeCreatesACreateEdgeLogEntry() {
    Edge edge = edge().withId("id")
                      .withProperty("rev", 1)
                      .withProperty("modified", changeWithTimestamp(1000L))
                      .build();
    LogEntryFactory instance = new LogEntryFactory();

    LogEntry entry = instance.createForEdge(edge);

    assertThat(entry, is(instanceOf(CreateEdgeLogEntry.class)));
  }

  private Change changeWithTimestamp(long timestamp) {
    Change change = new Change();
    change.setTimeStamp(timestamp);
    return change;
  }

  @Test
  public void createForEdgeCreatesAnUpdateEdgeLogEntryWhenTheEdgeHasARevHigherThan1() {
    UUID edgeId = UUID.randomUUID();
    Graph graph = newGraph().withVertex("v1", v -> v.withTimId("id1"))
                            .withVertex("v2",
                              v -> v.withOutgoingRelation("edge", "v1", e -> e.withTim_id(edgeId)
                                                                              .withRev(1)
                                                                              .withModified(changeWithTimestamp(1000L)))
                                    .withOutgoingRelation("edge", "v1", e -> e.withTim_id(edgeId)
                                                                              .withRev(2)
                                                                              .withModified(changeWithTimestamp(1000L)))
                            )
                            .build();
    Edge edge = graph.traversal().E().has("tim_id", edgeId.toString()).has("rev", 2).next();
    LogEntryFactory instance = new LogEntryFactory();

    LogEntry entry = instance.createForEdge(edge);

    assertThat(entry, is(instanceOf(UpdateEdgeLogEntry.class)));
  }

}
