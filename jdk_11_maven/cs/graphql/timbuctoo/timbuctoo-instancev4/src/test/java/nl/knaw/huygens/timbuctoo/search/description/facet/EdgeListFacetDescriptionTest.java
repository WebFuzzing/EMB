package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.util.VertexMatcher;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SearchRequestV2_1;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class EdgeListFacetDescriptionTest {


  private static final String FACET_NAME = "facetName";
  private static final String RELATION_NAME = "relationName";
  private EdgeListFacetDescription instance;

  @Before
  public void setUp() {
    instance = new EdgeListFacetDescription(FACET_NAME, RELATION_NAME);
  }

  @Test
  public void filterDoesNotAddFilterToTheGraphTraversalWhenTheFacetOfTheDescriptionIsNotPresent() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(),
            containsInAnyOrder(VertexMatcher.likeVertex().withTimId("1"), VertexMatcher.likeVertex().withTimId("2")));
  }

  @Test
  public void filterAddsAFilterToFilterOutTheNonMatchingVertices() {

    List<FacetValue> facetValues = Lists.newArrayList(
            new ListFacetValue(FACET_NAME, Lists.newArrayList(RELATION_NAME)));

    SearchRequestV2_1 searchRequest = new SearchRequestV2_1();
    searchRequest.setFacetValues(facetValues);

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("v1", v -> v.withTimId("1"))
            .withVertex("v2", v -> v.withTimId("2").withOutgoingRelation(RELATION_NAME, "v1"))
            .build().traversal().V();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), contains(VertexMatcher.likeVertex().withTimId("1")));
  }

  @Test
  public void filterAddsNoFilterIfTheFacetValuesIsEmpty() {
    List<FacetValue> facetValues = Lists.newArrayList(
            new ListFacetValue(FACET_NAME, Lists.newArrayList()));

    SearchRequestV2_1 searchRequest = new SearchRequestV2_1();
    searchRequest.setFacetValues(facetValues);
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(),
            containsInAnyOrder(VertexMatcher.likeVertex().withTimId("1"), VertexMatcher.likeVertex().withTimId("2")));
  }

  @Test
  public void getValuesAddsTheRelationNamesBelongingToTheVertext() {
    List<Vertex> vertices = newGraph()
            .withVertex("v1", v -> v.withTimId("1"))
            .withVertex("v2", v -> v.withTimId("2").withOutgoingRelation(RELATION_NAME, "v1"))
            .build().traversal().V().toList();

    Collections.sort(vertices, (vertexA, vertexB) ->
            ((String) vertexA.property("tim_id").value()).compareTo((String) vertexB.property("tim_id").value()));

    List<String> values = instance.getValues(vertices.get(0));
    List<String> values2 = instance.getValues(vertices.get(1));

    assertThat(values, contains(RELATION_NAME));
    assertThat(values2, equalTo(Lists.newArrayList()));
  }
}
