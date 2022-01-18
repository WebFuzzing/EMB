package nl.knaw.huygens.timbuctoo.search.description.facet;


import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class RelatedDatableRangeFacetDescriptionTest {

  public static final String FACET_NAME = "facetName";
  public static final String PROPERTY_NAME = "propertyName";
  public static final String RELATION = "relationName";

  private RelatedDatableRangeFacetDescription instance;

  @Before
  public void setUp() throws Exception {
    instance = new RelatedDatableRangeFacetDescription(FACET_NAME, PROPERTY_NAME, RELATION);
  }

  @Test
  public void filterAddsNoFilterWhenTheFacetIsNotPresent() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015-01")))
            .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("0015-01")))
            .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
            .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
            .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
            .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
            .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
            likeVertex().withTimId("id1"),
            likeVertex().withTimId("id2"),
            likeVertex().withTimId("id3"),
            likeVertex().withTimId("id4"),
            likeVertex().withTimId("id5"),
            likeVertex().withTimId("id6")
    ));
  }

  @Test
  public void filterAddFiltersTheVertices() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015-01")))
            .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("0015-01")))
            .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
            .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
            .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
            .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
            .build().traversal().V();

    List<FacetValue> facetValues = Lists.newArrayList(new DateRangeFacetValue(FACET_NAME, 101101L, 10001231L));

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
            likeVertex().withTimId("id2"),
            likeVertex().withTimId("id3")));
  }

  @Test
  public void filterIncludesTheVerticesWhereOnlyTheStartFallsInTheRange() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
          .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015/2020")))
          .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("0015-01")))
          .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
          .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
          .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
          .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
          .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList(new DateRangeFacetValue(FACET_NAME, 20140101L, 20160101L));

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), contains(likeVertex().withTimId("id1")));
  }

  @Test
  public void filterIncludesTheVerticesWhereOnlyTheEndFallsInTheRange() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
          .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015/2020")))
          .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("0015-01")))
          .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
          .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
          .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
          .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
          .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList(new DateRangeFacetValue(FACET_NAME, 20140101L, 20160224L));

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), contains(likeVertex().withTimId("id1")));
  }

  @Test
  public void filterAddNoFilterWhenTheFacetValueIsNotADateRangeFacetValue() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015-01")))
            .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("0015-01")))
            .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
            .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
            .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
            .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
            .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList((FacetValue) () -> FACET_NAME);

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
            likeVertex().withTimId("id1"),
            likeVertex().withTimId("id2"),
            likeVertex().withTimId("id3"),
            likeVertex().withTimId("id4"),
            likeVertex().withTimId("id5"),
            likeVertex().withTimId("id6")
    ));
  }

  @Test
  public void filterAddsAFilterIgnoresLastFourNumbers() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015-01")))
            .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("0015-01")))
            .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
            .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
            .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
            .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
            .build().traversal().V();
    List<FacetValue> facets = Lists.newArrayList(new DateRangeFacetValue(FACET_NAME, 20151001L, 20160101L));

    instance.filter(traversal, facets);

    assertThat(traversal.toList(), contains(likeVertex().withTimId("id1")));
  }

  @Test
  public void filterAddsAFilterThatIgnoresInvalidDatables() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withTimId("id4").withProperty(PROPERTY_NAME, asSerializedDatable("2015-01")))
            .withVertex("target2", v -> v.withTimId("id5").withProperty(PROPERTY_NAME, asSerializedDatable("invalid")))
            .withVertex("target3", v -> v.withTimId("id6").withProperty(PROPERTY_NAME, asSerializedDatable("0190-01")))
            .withVertex("source1", v -> v.withTimId("id1").withOutgoingRelation(RELATION, "target1"))
            .withVertex("source2", v -> v.withTimId("id2").withOutgoingRelation(RELATION, "target2"))
            .withVertex("source3", v -> v.withTimId("id3").withOutgoingRelation(RELATION, "target3"))
            .build().traversal().V();
    List<FacetValue> facets = Lists.newArrayList(new DateRangeFacetValue(FACET_NAME, 20151001L, 20160101L));

    instance.filter(traversal, facets);

    assertThat(traversal.toList(), contains(likeVertex().withTimId("id1")));
  }

  private String asSerializedDatable(String datableString) {
    return String.format("\"%s\"", datableString);
  }
}
