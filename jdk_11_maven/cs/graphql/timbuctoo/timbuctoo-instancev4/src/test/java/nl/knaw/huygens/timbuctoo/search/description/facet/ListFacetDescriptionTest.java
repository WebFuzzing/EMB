package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.util.VertexMatcher;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SearchRequestV2_1;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ListFacetDescriptionTest {

  public static final String FACET_NAME = "facetName";
  public static final String PROPERTY = "property";
  private PropertyParser propertyParser;
  private ListFacetDescription instance;

  @Before
  public void setUp() throws Exception {
    propertyParser = mock(PropertyParser.class);
    given(propertyParser.parse(anyString())).willAnswer(invocation -> invocation.getArguments()[0]);
    instance = new ListFacetDescription(FACET_NAME, PROPERTY, propertyParser);
  }

  @Test
  public void filterDoesNotAddFilterToTheGraphTraversalWhenTheFacetOfTheDescriptionIsNotPresent() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withProperty(PROPERTY, "value1").withTimId("1"))
      .withVertex(v -> v.withProperty(PROPERTY, "value2").withTimId("2"))
      .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(),
      containsInAnyOrder(VertexMatcher.likeVertex().withTimId("1"), VertexMatcher.likeVertex().withTimId("2")));
  }

  @Test
  public void filterAddsAFilterToFilterOutTheNonMatchingVertices() {
    String value1 = "value1";
    List<FacetValue> facetValues = Lists.newArrayList(
      new ListFacetValue(FACET_NAME, Lists.newArrayList(value1)));
    SearchRequestV2_1 searchRequest = new SearchRequestV2_1();
    searchRequest.setFacetValues(facetValues);
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withProperty(PROPERTY, value1).withTimId("1"))
      .withVertex(v -> v.withProperty(PROPERTY, "value2").withTimId("2"))
      .build().traversal().V();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), contains(VertexMatcher.likeVertex().withTimId("1")));
  }

  @Test
  public void filterAddsNoFilterIfTheFacetValuesIsEmpty() {
    String value1 = "value1";
    List<FacetValue> facetValues = Lists.newArrayList(
      new ListFacetValue(FACET_NAME, Lists.newArrayList()));
    SearchRequestV2_1 searchRequest = new SearchRequestV2_1();
    searchRequest.setFacetValues(facetValues);
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withProperty(PROPERTY, value1).withTimId("1"))
      .withVertex(v -> v.withProperty(PROPERTY, "value2").withTimId("2"))
      .build().traversal().V();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(),
      containsInAnyOrder(VertexMatcher.likeVertex().withTimId("1"), VertexMatcher.likeVertex().withTimId("2")));
  }

  @Test
  public void filterAddsNoFilterIfTheFacetValueIsNotAListFacetValue() {
    String value1 = "value1";
    List<FacetValue> facetValues = Lists.newArrayList((FacetValue) () -> FACET_NAME);
    SearchRequestV2_1 searchRequest = new SearchRequestV2_1();
    searchRequest.setFacetValues(facetValues);
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withProperty(PROPERTY, value1).withTimId("1"))
      .withVertex(v -> v.withProperty(PROPERTY, "value2").withTimId("2"))
      .build().traversal().V();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(),
      containsInAnyOrder(VertexMatcher.likeVertex().withTimId("1"), VertexMatcher.likeVertex().withTimId("2")));
  }

  @Test
  public void filterLetsTheParserParseEachDatabaseValue() {
    String value1 = "value1";
    List<FacetValue> facetValues = Lists.newArrayList(
      new ListFacetValue(FACET_NAME, Lists.newArrayList(value1)));
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withProperty(PROPERTY, "value1").withTimId("1"))
      .withVertex(v -> v.withProperty(PROPERTY, "value2").withTimId("2"))
      .build().traversal().V();

    instance.filter(traversal, facetValues);
    traversal.toList(); // needed to verify the parser

    verify(propertyParser).parse("value1");
    verify(propertyParser).parse("value2");
  }

}
