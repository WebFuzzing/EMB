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

public class DerivedPropertyValueGetterTest {

  private static final String PROPERTY_NOT_PRESENT = "propNotPresent";
  private static final String PROPERTY = "propPresent";
  private static final String[] RELATIONS = new String[]{"relation"};
  private static final String RELATION_NAME = "relation2";

  @Test
  public void getValuesReturnsEmptyListIfThePropertyIsNotPresent() {
    DerivedPropertyValueGetter instance = new DerivedPropertyValueGetter(RELATIONS, RELATION_NAME);

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("finalTarget1", v -> v.withProperty(PROPERTY_NOT_PRESENT, "value1"))
            .withVertex("finalTarget2", v -> v.withProperty(PROPERTY_NOT_PRESENT, "value2"))
            .withVertex("target1", v -> v.withOutgoingRelation(RELATION_NAME, "finalTarget1"))
            .withVertex("target2", v -> v.withOutgoingRelation(RELATION_NAME, "finalTarget2"))
            .withVertex(v -> v.withOutgoingRelation(RELATIONS[0], "target1"))
            .withVertex(v -> v.withOutgoingRelation(RELATIONS[0], "target2"))
            .build().traversal().V();

    List<List<String>> results = traversal.toList().stream()
            .map(v -> instance.getValues(v, PROPERTY))
            .filter(r -> r.size() > 0).collect(toList());

    assertThat(results, equalTo(Lists.newArrayList()));
  }

  @Test
  public void getValuesReturnsListOfStringsIfThePropertyIsPresent() {
    DerivedPropertyValueGetter instance = new DerivedPropertyValueGetter(RELATIONS, RELATION_NAME);

    GraphTraversal<Vertex, Vertex> traversal = newGraph()
            .withVertex("finalTarget1", v -> v.withProperty(PROPERTY, "value1"))
            .withVertex("target1", v -> v.withOutgoingRelation(RELATIONS[0], "finalTarget1"))
            .withVertex(v -> v.withOutgoingRelation(RELATION_NAME, "target1"))
            .build().traversal().V();

    List<List<String>> results = traversal.toList().stream()
            .map(v -> instance.getValues(v, PROPERTY))
            .filter(r -> r.size() > 0).collect(toList());

    assertThat(results.get(0), contains(is("value1")));
  }
}
