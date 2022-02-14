package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ChangeRangeFacetDescriptionTest {

  public static final String FACET_NAME = "facetName";
  public static final String PROPERTY_NAME = "propertyName";
  public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");
  private ChangeRangeFacetDescription instance;

  @Before
  public void setUp() throws Exception {
    instance = new ChangeRangeFacetDescription(FACET_NAME, PROPERTY_NAME);
  }

  @Test
  public void filterAddsNoFilterWhenTheFacetIsNotPresent() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedChangeWithDate("20150101")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedChangeWithDate("10000302")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedChangeWithDate("21000302")))
      .build().traversal().V();
    List<FacetValue> facets = Lists.newArrayList();

    instance.filter(traversal, facets);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  @Test
  public void filterAddsAFilterThatChecksIfTheDateIsBetweenTheUpperAndLowerLimit() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedChangeWithDate("20150101")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedChangeWithDate("10000302")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedChangeWithDate("21000302")))
      .build().traversal().V();
    List<FacetValue> facets = Lists.newArrayList(new DateRangeFacetValue(FACET_NAME, 20000101, 20160101));

    instance.filter(traversal, facets);

    assertThat(traversal.toList(), contains(likeVertex().withTimId("id1")));
  }

  @Test
  public void filterAddsNoFilterWhenTheFacetIsOfTheWrongType() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY_NAME, serializedChangeWithDate("20150101")))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY_NAME, serializedChangeWithDate("10000302")))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_NAME, serializedChangeWithDate("21000302")))
      .build().traversal().V();
    List<FacetValue> facets = Lists.newArrayList((FacetValue) () -> FACET_NAME);

    instance.filter(traversal, facets);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  /**
   * Serializes a Change with a json object mapper.
   *
   * @param dateString a date in the format yyyyMMdd
   * @return the serialized Change
   */
  private String serializedChangeWithDate(String dateString) {
    try {
      LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
      Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
      Change change = new Change(instant.toEpochMilli(), "notImportant", "notImportant");

      return new ObjectMapper().writeValueAsString(change);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
