package nl.knaw.huygens.timbuctoo.databaselog;

import nl.knaw.huygens.timbuctoo.databaselog.entry.LogEntryFactory;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.shaded.jackson.core.JsonProcessingException;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.EdgeMatcher.likeEdge;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class DatabaseLogIntegrationTest {
  @Test
  public void generateFirstAddsTheVerticesAndThenAddsTheEdgesForATimeStamp() {
    LogOutput logOutput = mock(LogOutput.class);
    UUID rel1Uuid = UUID.fromString("ff65089c-2ded-4af0-95e7-0476979f96b8");
    UUID rel2Uuid = UUID.fromString("a628b090-ec7f-4608-9356-61728355ad5a");
    GraphWrapper graphWrapper = newGraph()
      .withVertex("v1", v -> v.withTimId("id1")
                              .withProperty("modified", changeStringWithTimestamp(1000L))
                              .withProperty("rev", 1)
      )
      .withVertex("v2", v -> v.withTimId("id2")
                              .withProperty("modified", changeStringWithTimestamp(1001L))
                              .withProperty("rev", 1)
                              .withOutgoingRelation("isRelatedTo", "v2", r -> r.withTim_id(rel1Uuid)
                                                                               .withModified(changeWithTimestamp(1002L))
                                                                               .withRev(1)
                              )
      )
      .withVertex("v3", v -> v.withTimId("id3")
                              .withProperty("modified", changeStringWithTimestamp(1002L))
                              .withProperty("rev", 1)
      )
      .withVertex("v4", v -> v.withTimId("id3")
                              .withProperty("modified", changeStringWithTimestamp(4000L))
                              .withProperty("rev", 2)
                              .withIncomingRelation("isRelatedTo", "v1", r -> r.withTim_id(rel2Uuid)
                                                                               .withModified(changeWithTimestamp(3000L))
                                                                               .withRev(1)
                              )
                              .withIncomingRelation("VERSION_OF", "v3")
      ).wrap();
    DatabaseLog logGenerator =
      new DatabaseLog(graphWrapper, new LogEntryFactory(), logOutput);

    logGenerator.generate();

    InOrder inOrder = inOrder(logOutput);
    inOrder.verify(logOutput).newVertex(argThat(likeVertex().withTimId("id1")));
    inOrder.verify(logOutput).newVertex(argThat(likeVertex().withTimId("id2")));
    inOrder.verify(logOutput).newVertex(argThat(likeVertex().withTimId("id3")));
    inOrder.verify(logOutput).newEdge(argThat(likeEdge().withId(rel1Uuid.toString())));
    inOrder.verify(logOutput).newEdge(argThat(likeEdge().withId(rel2Uuid.toString())));
    inOrder.verify(logOutput).updateVertex(argThat(likeVertex().withTimId("id3")));
  }

  private String changeStringWithTimestamp(long timeStamp) {
    try {
      return new ObjectMapper().writeValueAsString(changeWithTimestamp(timeStamp));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Change changeWithTimestamp(long timeStamp) {
    return new Change(timeStamp, "", "");
  }
}
