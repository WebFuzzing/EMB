package nl.knaw.huygens.timbuctoo.databaselog;

import nl.knaw.huygens.timbuctoo.databaselog.entry.LogEntryFactory;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.core.JsonProcessingException;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.EdgeMatcher.likeEdge;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DatabaseLogTest {

  private ObjectMapper objectMapper;
  private LogEntryFactory logEntryFactory;
  private LogEntry vertexLogEntry;

  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    logEntryFactory = mock(LogEntryFactory.class);
    vertexLogEntry = mock(LogEntry.class);
    given(logEntryFactory.createForVertex(any(Vertex.class))).willReturn(vertexLogEntry);
    given(logEntryFactory.createForEdge(any(Edge.class))).willReturn(mock(LogEntry.class));
  }

  @Test
  public void generateIteratesThroughAllVerticesOrderedByModifiedDate() throws Exception {
    String first = "first";
    String second = "second";
    String third = "third";
    GraphWrapper graphWrapper = newGraph()
      .withVertex(v -> v.withTimId(first)
                        .withProperty("modified", getChangeStringWithTimestamp(1464346423L))
                        .withProperty("rev", 1)
      )
      .withVertex(v -> v.withTimId(third).withProperty("modified",
        getChangeStringWithTimestamp(1464346425L))
                        .withProperty("rev", 1)
      )
      .withVertex(v -> v.withTimId(second).withProperty("modified",
        getChangeStringWithTimestamp(1464346424L))
                        .withProperty("rev", 1)
      )
      .wrap();
    DatabaseLog instance = createInstance(graphWrapper);

    instance.generate();

    InOrder inOrder = inOrder(logEntryFactory);
    inOrder.verify(logEntryFactory).createForVertex(argThat(likeVertex().withTimId(first)));
    inOrder.verify(logEntryFactory).createForVertex(argThat(likeVertex().withTimId(second)));
    inOrder.verify(logEntryFactory).createForVertex(argThat(likeVertex().withTimId(third)));
  }


  @Test
  public void generateAppendsAVertexEntryForEachVertexToTheLog() throws Exception {
    GraphWrapper graphWrapper = newGraph()
      .withVertex(v -> v.withTimId("id1")
                        .withProperty("modified", getChangeStringWithTimestamp(1464346423L))
                        .withProperty("rev", 1)
      )
      .withVertex(v -> v.withTimId("id2")
                        .withProperty("modified", getChangeStringWithTimestamp(1464346425L))
                        .withProperty("rev", 1)
      )
      .withVertex(v -> v.withTimId("id3")
                        .withProperty("modified", getChangeStringWithTimestamp(1464346424L))
                        .withProperty("rev", 1)
      )
      .wrap();
    DatabaseLog instance = createInstance(graphWrapper);

    instance.generate();

    verify(vertexLogEntry, times(3)).appendToLog(any(LogOutput.class));
  }

  @Test
  public void generateAppendsEachVersionOfAVertexOnlyOnce() {
    GraphWrapper graphWrapper = newGraph()
      .withVertex(v -> v.withTimId("id1")
                        .withProperty("modified", getChangeStringWithTimestamp(1464346423L))
                        .withProperty("rev", 1)
      )
      .withVertex(v -> v.withTimId("id1")
                        .withProperty("modified", getChangeStringWithTimestamp(1464346425L))
                        .withProperty("rev", 2)
      )
      .withVertex(v -> v.withTimId("id1")
                        .withProperty("modified", getChangeStringWithTimestamp(1464346424L))
                        .withProperty("rev", 2)
      )
      .wrap();
    DatabaseLog instance = createInstance(graphWrapper);

    instance.generate();

    verify(logEntryFactory).createForVertex(argThat(likeVertex().withTimId("id1").withProperty("rev", 1)));
    verify(logEntryFactory, times(1)).createForVertex(argThat(likeVertex().withTimId("id1").withProperty("rev", 2)));
  }

  @Test
  public void generateAppendsEachVersionOfAnEdgeOnlyOnce() {
    UUID relId = UUID.fromString("ff65089c-2ded-4af0-95e7-0476979f96b8");
    GraphWrapper graphWrapper = newGraph()
      .withVertex("v1", v -> v.withTimId("id1")
                              .withProperty("modified", getChangeStringWithTimestamp(1464346423L))
                              .withProperty("rev", 3)
      )
      .withVertex("v2", v -> v.withTimId("id2")
                              .withProperty("modified", getChangeStringWithTimestamp(1464346425L))
                              .withProperty("rev", 3)
                              .withOutgoingRelation("relatedTo", "v1",
                                r -> r.withTim_id(relId).withRev(1)
                                      .withModified(changeWithTimestamp(1464346426L))
                              )
                              .withOutgoingRelation("relatedTo", "v1",
                                r -> r.withTim_id(relId).withRev(2)
                                      .withModified(changeWithTimestamp(1464346428L))
                              )
                              .withOutgoingRelation("relatedTo", "v1",
                                r -> r.withTim_id(relId).withRev(2)
                                      .withModified(changeWithTimestamp(1464346428L))
                              )
      )
      .wrap();
    DatabaseLog instance = createInstance(graphWrapper);

    instance.generate();

    verify(logEntryFactory).createForEdge(argThat(likeEdge().withId(relId.toString()).withProperty("rev", 1)));
    verify(logEntryFactory, times(1)).createForEdge(argThat(likeEdge().withId(relId.toString())
                                                                      .withProperty("rev", 2)));
  }

  private DatabaseLog createInstance(GraphWrapper graphWrapper) {
    return new DatabaseLog(graphWrapper, logEntryFactory);
  }

  private String getChangeStringWithTimestamp(long timeStamp) {
    try {
      return objectMapper.writeValueAsString(changeWithTimestamp(timeStamp));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Change changeWithTimestamp(long timeStamp) {
    return new Change(timeStamp, "", "");
  }
}
