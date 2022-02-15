package nl.knaw.huygens.timbuctoo.databaselog;

import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.core.JsonProcessingException;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.EdgeMatcher.likeEdge;
import static nl.knaw.huygens.hamcrest.OptionalPresentMatcher.present;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

public class DatabaseFixerTest {

  @Test
  public void fixAddsMissingEdges() {
    UUID rel1Id = UUID.fromString("ff65089c-2ded-4af0-95e7-0476979f96b8");
    UUID rel2Id = UUID.fromString("a628b090-ec7f-4608-9356-61728355ad5a");
    GraphWrapper wrapper = newGraph().withVertex("v1", v -> v.withTimId("id1"))
                                     .withVertex("v2", v -> v.withTimId("id2")
                                                             .withOutgoingRelation("relatedTo", "v1",
                                                               r -> r.withTim_id(rel1Id).withRev(2)
                                                                     .withIsLatest(true)
                                                                     .withModified(changeWithTimestamp(10000L))
                                                                     .withCreated(changeWithTimestamp(1500L))
                                                             )
                                     )
                                     .withVertex("v3", v -> v.withTimId("id3")
                                                             .withOutgoingRelation("relatedTo", "v2",
                                                               r -> r.withTim_id(rel2Id).withRev(4)
                                                                     .withIsLatest(true)
                                                                     .withModified(changeWithTimestamp(10000L))
                                                                     .withCreated(changeWithTimestamp(1000L))
                                                             )
                                     )
                                     .wrap();
    DatabaseFixer databaseFixer = new DatabaseFixer(wrapper);

    databaseFixer.fix();

    assertThat(wrapper.getGraph().traversal().E().has("tim_id", rel1Id.toString()).toList(), containsInAnyOrder(
      likeEdge().withId(rel1Id.toString()).withProperty("rev", 1).withModifiedTimestamp(1500L),
      likeEdge().withId(rel1Id.toString()).withProperty("rev", 2).withModifiedTimestamp(10000L)
    ));
    assertThat(wrapper.getGraph().traversal().E().has("tim_id", rel2Id.toString()).toList(), containsInAnyOrder(
      likeEdge().withId(rel2Id.toString()).withProperty("rev", 1).withModifiedTimestamp(1000L),
      likeEdge().withId(rel2Id.toString()).withProperty("rev", 2).withModifiedTimestamp(1000L),
      likeEdge().withId(rel2Id.toString()).withProperty("rev", 3).withModifiedTimestamp(1000L),
      likeEdge().withId(rel2Id.toString()).withProperty("rev", 4).withModifiedTimestamp(10000L)
    ));
  }

  @Test
  public void fixSetsTheAcceptedPropertiesToTrue() {
    UUID rel1Id = UUID.fromString("ff65089c-2ded-4af0-95e7-0476979f96b8");
    GraphWrapper wrapper = newGraph().withVertex("v1", v -> v.withTimId("id1"))
                                     .withVertex("v2", v -> v.withTimId("id2")
                                                             .withOutgoingRelation("relatedTo", "v1",
                                                               r -> r.withTim_id(rel1Id).withRev(2)
                                                                     .withIsLatest(true)
                                                                     .withModified(changeWithTimestamp(10000L))
                                                                     .withCreated(changeWithTimestamp(1500L))
                                                                     .withAccepted("vrerel", false)
                                                             )
                                     )
                                     .wrap();
    DatabaseFixer databaseFixer = new DatabaseFixer(wrapper);

    databaseFixer.fix();

    assertThat(wrapper.getGraph().traversal().E().has("tim_id", rel1Id.toString()).toList(), containsInAnyOrder(
      likeEdge().withId(rel1Id.toString()).withProperty("rev", 1).withProperty("vrerel_accepted", true),
      likeEdge().withId(rel1Id.toString()).withProperty("rev", 2).withProperty("vrerel_accepted", false)
    ));
  }

  @Test
  public void fixAddsMissingVertices() {
    String vertex1CreatedProp = changeStringWithTimestamp(1500L);
    String vertex2CreatedProp = changeStringWithTimestamp(1400L);
    GraphWrapper graphWrapper = newGraph().withVertex("v1", v -> v.withTimId("id1")
                                                                  .withProperty("rev", 2)
                                                                  .withProperty("modified",
                                                                    changeStringWithTimestamp(10000L))
                                                                  .withProperty("created", vertex1CreatedProp)
                                                                  .withProperty("isLatest", true))
                                          .withVertex("v2", v -> v.withTimId("id2").withProperty("rev", 3)
                                                                  .withProperty("modified",
                                                                    changeStringWithTimestamp(10000L))
                                                                  .withProperty("created", vertex2CreatedProp)
                                                                  .withProperty("isLatest", true))
                                          .wrap();
    DatabaseFixer instance = new DatabaseFixer(graphWrapper);

    instance.fix();

    assertThat(graphWrapper.getGraph().traversal().V().has("tim_id", "id1").toList(), containsInAnyOrder(
      likeVertex().withTimId("id1").withProperty("rev", 1).withProperty("modified", vertex1CreatedProp),
      likeVertex().withTimId("id1").withProperty("rev", 2)
    ));
    assertThat(graphWrapper.getGraph().traversal().V().has("tim_id", "id2").toList(), containsInAnyOrder(
      likeVertex().withTimId("id2").withProperty("rev", 1).withProperty("modified", vertex2CreatedProp),
      likeVertex().withTimId("id2").withProperty("rev", 2).withProperty("modified", vertex2CreatedProp),
      likeVertex().withTimId("id2").withProperty("rev", 3)
    ));
  }

  @Test
  public void fixAddsAVersionOfRelation() {
    String vertex1CreatedProp = changeStringWithTimestamp(1500L);
    GraphWrapper graphWrapper = newGraph().withVertex("v1", v -> v.withTimId("id1")
                                                                  .withProperty("rev", 2)
                                                                  .withProperty("modified",
                                                                    changeStringWithTimestamp(10000L))
                                                                  .withProperty("created", vertex1CreatedProp)
                                                                  .withProperty("isLatest", true))
                                          .wrap();
    DatabaseFixer instance = new DatabaseFixer(graphWrapper);

    instance.fix();


    Optional<Vertex> vertex = graphWrapper.getGraph().traversal().V().has("tim_id", "id1").has("rev", 1).tryNext();
    assertThat(vertex, is(present()));
    Iterator<Edge> versionOfRelations = vertex.get().edges(Direction.OUT, "VERSION_OF");
    assertThat(versionOfRelations.hasNext(), is(true));
  }

  private String changeStringWithTimestamp(long timestamp) {
    try {
      return new ObjectMapper().writeValueAsString(changeWithTimestamp(timestamp));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Change changeWithTimestamp(long timestamp) {
    return new Change(timestamp, "", "");
  }
}
