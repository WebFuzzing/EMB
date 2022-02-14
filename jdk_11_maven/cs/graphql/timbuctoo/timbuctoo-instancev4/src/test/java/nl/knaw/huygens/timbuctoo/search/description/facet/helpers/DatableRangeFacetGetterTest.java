package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetGetter;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class DatableRangeFacetGetterTest {
  private static final String FACET_NAME = "facet_name";

  @Test
  public void getFacetReturnsLowerAndUpperLimitAsZeroWhenValueKeyIsNotParsableAsDatableProp() {
    FacetGetter instance = new DatableRangeFacetGetter();
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
    FacetGetter instance = new DatableRangeFacetGetter();
    Map<String, Set<Vertex>> values = Maps.newHashMap();
    List<Vertex> vertices1 = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V().toList();
    List<Vertex> vertices2 = newGraph()
            .withVertex(v -> v.withTimId("3"))
            .build().traversal().V().toList();
    values.put(asSerializedDatable("2015-01-01"), Sets.newHashSet(vertices1));
    values.put("{\"nonParsable\": null}", Sets.newHashSet(vertices1));
    values.put(asSerializedDatable("1000-03-02"), Sets.newHashSet(vertices2));
    values.put(asSerializedDatable("2100-03-02"), Sets.newHashSet(vertices2));

    Facet facet = instance.getFacet(FACET_NAME, values);

    assertThat(facet.getName(), equalTo(FACET_NAME));
    assertThat(facet.getOptions(),
            containsInAnyOrder(new Facet.RangeOption(10000302, 21000302)));
  }

  private String asSerializedDatable(String datableString) {
    return String.format("\"%s\"", datableString);
  }

}
