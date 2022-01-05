package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;


import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class LocalPropertyValueGetterTest {

  private static final String PROPERTY_NOT_PRESENT = "propNotPresent";
  private static final String PROPERTY = "propPresent";

  @Test
  public void getValuesReturnsNullIfThePropertyIsNotPresent() {
    LocalPropertyValueGetter instance = new LocalPropertyValueGetter();

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex(v -> v.withProperty(PROPERTY_NOT_PRESENT, "value1").withTimId("1"))
            .withVertex(v -> v.withProperty(PROPERTY_NOT_PRESENT, "value2").withTimId("2"))
            .build().traversal().V();

    List<List<String>> results = traversal.toList().stream()
            .map(v -> instance.getValues(v, PROPERTY)).collect(toList());

    assertThat(results, contains(equalTo(null), equalTo(null)));
  }

  @Test
  public void getValuesReturnsListOfStringsIfThePropertyIsPresent() {
    LocalPropertyValueGetter instance = new LocalPropertyValueGetter();

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex(v -> v.withProperty(PROPERTY, "value1").withTimId("1"))
            .build().traversal().V();

    List<List<String>> results = traversal.toList().stream()
            .map(v -> instance.getValues(v, PROPERTY)).collect(toList());

    assertThat(results.get(0), contains(is("value1")));
  }
}
