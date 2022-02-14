package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.google.common.collect.Lists;
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

public class RelatedPropertyValueGetterTest {

  private static final String PROPERTY_NOT_PRESENT = "propNotPresent";
  private static final String PROPERTY = "propPresent";
  private static final String RELATION = "relation";

  @Test
  public void getValuesReturnsEmptyListIfThePropertyIsNotPresent() {
    RelatedPropertyValueGetter instance = new RelatedPropertyValueGetter(RELATION);

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withProperty(PROPERTY_NOT_PRESENT, "value1"))
            .withVertex("target2", v -> v.withProperty(PROPERTY_NOT_PRESENT, "value2"))
            .withVertex(v -> v.withOutgoingRelation(RELATION, "target1"))
            .withVertex(v -> v.withOutgoingRelation(RELATION, "target2"))
            .build().traversal().V();

    List<List<String>> results = traversal.toList().stream()
            .map(v -> instance.getValues(v, PROPERTY)).collect(toList());

    assertThat(results, contains(
            equalTo(Lists.newArrayList()),
            equalTo(Lists.newArrayList()),
            equalTo(Lists.newArrayList()),
            equalTo(Lists.newArrayList())
    ));
  }

  @Test
  public void getValuesReturnsListOfStringsIfThePropertyIsPresent() {
    RelatedPropertyValueGetter instance = new RelatedPropertyValueGetter(RELATION);

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("target1", v -> v.withProperty(PROPERTY, "value1"))
            .withVertex(v -> v.withOutgoingRelation(RELATION, "target1"))
            .build().traversal().V();

    List<List<String>> results = traversal.toList().stream()
            .map(v -> instance.getValues(v, PROPERTY))
            .filter(r -> r.size() > 0).collect(toList());

    assertThat(results.get(0), contains(is("value1")));
  }
}
