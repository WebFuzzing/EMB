package nl.knaw.huygens.timbuctoo.search.description.sort;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;
import static nl.knaw.huygens.timbuctoo.search.description.sort.BuildableSortFieldDescription.newSortFieldDescription;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class SortFieldDescriptionTest {
  public static final String PROPERTY_2 = "property2";
  private static final String PROPERTY = "property";

  @Test
  public void getTraversalReturnsTheTraversalToOrderThePropertyValues() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY, "123"))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY, "1234"))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY, "12"))
      .build()
      .traversal()
      .V();
    SortFieldDescription instance = newSortFieldDescription()
      .withName("name")
      .withDefaultValue("")
      .withProperty(localProperty().withName(PROPERTY))
      .build();

    GraphTraversal<?, ?> orderTraversal = instance.getTraversal().get(0);

    List<Vertex> vertices = traversal.order().by(orderTraversal, Order.incr).toList();
    assertThat(vertices, contains(
      likeVertex().withTimId("id3"),
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2")));
  }

  @Test
  public void getTraversalAddsATraversalWithADefaultValueForTheVerticesWithoutTheProperty() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY, "123"))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY, "1234"))
      .withVertex(v -> v.withTimId("id3"))
      .build()
      .traversal()
      .V();
    SortFieldDescription instance = newSortFieldDescription()
      .withName("name")
      .withDefaultValue("")
      .withProperty(localProperty().withName(PROPERTY))
      .build();

    GraphTraversal<?, ?> orderTraversal = instance.getTraversal().get(0);

    List<Vertex> vertices = traversal.order().by(orderTraversal, Order.incr).toList();
    assertThat(vertices, contains(
      likeVertex().withTimId("id3"),
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2")));
  }

  @Test
  public void getTraversalReturnsATraversalWithABackupTraversalWhenConfigured() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY, "abc"))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY, "abcd"))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY_2, "efgh"))
      .withVertex(v -> v.withTimId("id4").withProperty(PROPERTY_2, "ab"))
      .build()
      .traversal()
      .V();
    SortFieldDescription instance = newSortFieldDescription()
      .withName("name")
      .withDefaultValue("")
      .withProperty(localProperty().withName(PROPERTY))
      .withBackupProperty(localProperty().withName(PROPERTY_2))
      .build();

    GraphTraversal<?, ?> orderTraversal = instance.getTraversal().get(0);

    List<Vertex> vertices = traversal.order().by(orderTraversal, Order.incr).toList();
    assertThat(vertices, contains(
      likeVertex().withTimId("id4"),
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }
}
