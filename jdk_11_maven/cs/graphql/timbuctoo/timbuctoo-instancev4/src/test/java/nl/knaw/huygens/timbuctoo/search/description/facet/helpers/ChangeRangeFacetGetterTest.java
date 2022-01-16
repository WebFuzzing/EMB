package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetGetter;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ChangeRangeFacetGetterTest {
  private static final String FACET_NAME = "facet_name";

  @Test
  public void getFacetReturnsLowerAndUpperLimitAsZeroWhenValueKeyIsNotParsableAsModifiedProp() {
    FacetGetter instance = new ChangeRangeFacetGetter();
    Map<String, Set<Vertex>> values = Maps.newHashMap();
    List<Vertex> vertices1 = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V().toList();
    List<Vertex> vertices2 = newGraph()
            .withVertex(v -> v.withTimId("3"))
            .build().traversal().V().toList();
    values.put("val1", Sets.newHashSet(vertices1));
    values.put("val2", Sets.newHashSet(vertices2));

    Facet facet = instance.getFacet(FACET_NAME, values);

    assertThat(facet.getName(), equalTo(FACET_NAME));
    assertThat(facet.getOptions(),
            containsInAnyOrder(new Facet.RangeOption(0, 0)));
  }

  @Test
  public void getFacetReturnsLowerAndUpperLimit() {
    FacetGetter instance = new ChangeRangeFacetGetter();
    Map<String, Set<Vertex>> values = Maps.newHashMap();
    List<Vertex> vertices1 = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V().toList();
    List<Vertex> vertices2 = newGraph()
            .withVertex(v -> v.withTimId("3"))
            .build().traversal().V().toList();
    values.put(serializedChangeWithDate("20150101"), Sets.newHashSet(vertices1));
    values.put("{\"nonParsable\": null}", Sets.newHashSet(vertices1));
    values.put(serializedChangeWithDate("20121231"), Sets.newHashSet(vertices2));
    values.put(serializedChangeWithDate("20131231"), Sets.newHashSet(vertices2));

    Facet facet = instance.getFacet(FACET_NAME, values);

    assertThat(facet.getName(), equalTo(FACET_NAME));
    assertThat(facet.getOptions(),
            containsInAnyOrder(new Facet.RangeOption(20121231, 20150101)));
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
