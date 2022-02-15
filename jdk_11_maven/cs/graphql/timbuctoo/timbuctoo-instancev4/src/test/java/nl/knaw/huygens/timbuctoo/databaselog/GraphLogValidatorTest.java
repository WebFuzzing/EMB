package nl.knaw.huygens.timbuctoo.databaselog;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.Writer;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class GraphLogValidatorTest {

  public static final String VERTEX_ID_1 = "id1";
  public static final String VERTEX_ID_2 = "id2";
  public static final UUID REL_1_ID = UUID.fromString("ff65089c-2ded-4af0-95e7-0476979f96b8");
  public static final UUID REL_2_ID = UUID.fromString("a628b090-ec7f-4608-9356-61728355ad5a");

  @Test
  public void writeReportWritesALineForEachInvalidVertex() throws Exception {
    GraphWrapper wrapper = newGraph().withVertex(v -> v.withTimId(VERTEX_ID_1)
                                                       .withProperty("rev", 1)
                                                       .withLabel("document"))
                                     .withVertex(v -> v.withTimId(VERTEX_ID_2)
                                                       .withProperty("rev", 1)
                                                       .withLabel("document"))
                                     .withVertex(v -> v.withTimId(VERTEX_ID_1)
                                                       .withProperty("rev", 2)
                                                       .withLabel("document"))
                                     .withVertex(v -> v.withTimId(VERTEX_ID_2)
                                                       .withProperty("rev", 2)
                                                       .withLabel("document"))
                                     .withVertex(v -> v.withLabel("history")
                                                       .withOutgoingRelation("NEXT_ITEM", "v1"))
                                     .withVertex("v1", v -> v.withProperty("TIM_tim_id", VERTEX_ID_1)
                                                             .withProperty("rev", 1)
                                                             .withLabel("createVertexEntry")
                                                             .withOutgoingRelation("NEXT_ITEM", "v2")
                                     )
                                     .withVertex("v2", v -> v.withProperty("TIM_tim_id", VERTEX_ID_1)
                                                             .withProperty("rev", 2)
                                                             .withLabel("updateVertexEntry"))
                                     .wrap();
    GraphLogValidator graphLogValidator = new GraphLogValidator(wrapper);
    Writer writer = mock(Writer.class);

    graphLogValidator.writeReport(writer);

    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(writer, times(2)).write(stringArgumentCaptor.capture());
    assertThat(stringArgumentCaptor.getAllValues(), containsInAnyOrder(
        stringContainsInOrder(Lists.newArrayList("Vertex", VERTEX_ID_2, "1")),
        stringContainsInOrder(Lists.newArrayList("Vertex", VERTEX_ID_2, "2"))
    ));
    verifyNoMoreInteractions(writer);
  }

  @Test
  public void writeReportWritesALineForEachInvalidEdge() throws Exception {
    GraphWrapper wrapper = newGraph().withVertex("v1", v -> v.withTimId(VERTEX_ID_1)
                                                             .withProperty("rev", 1)
                                                             .withLabel("document"))
                                     .withVertex("v2", v -> v.withTimId(VERTEX_ID_2)
                                                             .withProperty("rev", 1)
                                                             .withLabel("document")
                                                             .withOutgoingRelation("relatedTo", "v1",
                                                               r -> r.withTim_id(REL_1_ID)
                                                                     .withRev(1)
                                                             )
                                                             .withOutgoingRelation("relatedTo", "v1",
                                                               r -> r.withTim_id(REL_1_ID)
                                                                     .withRev(2)
                                                             )
                                                             .withOutgoingRelation("otherRelation", "v1",
                                                               r -> r.withTim_id(REL_2_ID)
                                                                     .withRev(1))
                                                             .withOutgoingRelation("otherRelation", "v1",
                                                               r -> r.withTim_id(REL_2_ID)
                                                                     .withRev(2))
                                     )
                                     .withVertex("v3", v -> v.withLabel("createVertexEntry")
                                                             .withProperty("TIM_tim_id", VERTEX_ID_1)
                                                             .withProperty("rev", 1)
                                                             .withOutgoingRelation("NEXT_ITEM", "v4")
                                     )
                                     .withVertex("v4", v -> v.withLabel("updateVertexEntry")
                                                             .withProperty("TIM_tim_id", VERTEX_ID_2)
                                                             .withProperty("rev", 1)
                                                             .withOutgoingRelation("NEXT_ITEM", "v5")

                                     )
                                     .withVertex("v5", v -> v.withLabel("createEdgeEntry")
                                                             .withProperty("TIM_tim_id", REL_2_ID.toString())
                                                             .withProperty("rev", 1)
                                                             .withOutgoingRelation("NEXT_ITEM", "v6")
                                     )
                                     .withVertex("v6", v -> v.withLabel("updateEdgeEntry")
                                                             .withProperty("TIM_tim_id", REL_2_ID.toString())
                                                             .withProperty("rev", 2)
                                     )
                                     .wrap();
    GraphLogValidator graphLogValidator = new GraphLogValidator(wrapper);
    Writer writer = mock(Writer.class);

    graphLogValidator.writeReport(writer);

    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(writer, times(2)).write(stringArgumentCaptor.capture());
    assertThat(stringArgumentCaptor.getAllValues(), containsInAnyOrder(
      stringContainsInOrder(Lists.newArrayList("Edge", REL_1_ID.toString(), "1")),
      stringContainsInOrder(Lists.newArrayList("Edge", REL_1_ID.toString(), "2"))));

    verifyNoMoreInteractions(writer);
  }

}
