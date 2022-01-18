package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class MultiValueListFacetDescriptionTest {

  public static final String FACET_NAME = "facetName";
  public static final String PROPERTY_NAME = "propertyName";
  private ObjectMapper objectMapper;
  private MultiValueListFacetDescription instance;

  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    instance = new MultiValueListFacetDescription(FACET_NAME, PROPERTY_NAME);
  }

  @Test
  public void filterDoesNotFilterWhenFacetValuesDoesNotContainTheFacet() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedListOf("value1", "value2")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedListOf("value2", "value3")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedListOf("value1", "value3", "value4")))
      .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  @Test
  public void filterFiltersAllTheVerticesThatDoNotContainTheFacetValue() {
    String value1 = "value1";
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedListOf(value1, "value2")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedListOf("value2", "value3")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedListOf(value1, "value3", "value4")))
      .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList(new ListFacetValue(FACET_NAME, Lists.newArrayList(value1)));

    instance.filter(traversal, facetValues);

    List<Vertex> actual = traversal.toList();
    assertThat(actual, containsInAnyOrder(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id3")));
  }

  @Test
  public void filterDoesNotAddAFilterWhenTheFacetValueIsNotAListFacetValue() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedListOf("value1", "value2")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedListOf("value2", "value3")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedListOf("value1", "value3", "value4")))
      .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList((FacetValue) () -> FACET_NAME);

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  @Test
  public void filterDoesNotAddAFilterWhenTheFacetValueContainsNoValues() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedListOf("value1", "value2")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedListOf("value2", "value3")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedListOf("value1", "value3", "value4")))
      .build().traversal().V();
    List<FacetValue> facetValues = Lists.newArrayList(new ListFacetValue(FACET_NAME, Lists.newArrayList()));

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  @Test
  public void filterAddsAFilterThatChecksIfTheVerticesContainOnOfTheValues() {
    String value3 = "value3";
    String value4 = "value4";
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedListOf("value1", "value2")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedListOf("value2", value3)))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedListOf("value1", value4)))
      .build().traversal().V();
    List<String> values = Lists.newArrayList(value3, value4);
    List<FacetValue> facetValues = Lists.newArrayList(new ListFacetValue(FACET_NAME, values));

    instance.filter(traversal, facetValues);

    assertThat(traversal
      .toList(), containsInAnyOrder(
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  private String serializedListOf(String... values) {
    List<String> list = Arrays.asList(values);

    try {
      return objectMapper.writeValueAsString(list);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
