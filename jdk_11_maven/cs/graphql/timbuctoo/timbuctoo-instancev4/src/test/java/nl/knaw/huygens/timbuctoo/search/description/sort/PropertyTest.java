package nl.knaw.huygens.timbuctoo.search.description.sort;

import nl.knaw.huygens.timbuctoo.search.description.Property;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;

import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class PropertyTest {

  public static final String PROPERTY = "property";

  @Test
  public void getTraversalReturnsATraversalThatOrdersByStringValue() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY, "123"))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY, "1234"))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY, "254"))
      .build()
      .traversal()
      .V();
    Property instance = Property.localProperty().withName(PROPERTY).build();

    GraphTraversal<?, ?> orderTraversal = instance.getTraversal();

    List<Vertex> vertices = traversal.order().by(orderTraversal, Order.incr).toList();
    assertThat(vertices, contains(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id2"),
      likeVertex().withTimId("id3")));
  }

  @Test
  public void getTraversalReturnsATraversalThatLetsTheParserDetermineTheOrder() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(v -> v.withTimId("id1").withProperty(PROPERTY, "123"))
      .withVertex(v -> v.withTimId("id2").withProperty(PROPERTY, "1234"))
      .withVertex(v -> v.withTimId("id3").withProperty(PROPERTY, "254"))
      .build()
      .traversal()
      .V();
    Property instance = Property.localProperty().withName(PROPERTY).withParser(new IntegerParser()).build();

    GraphTraversal<?, ?> orderTraversal = instance.getTraversal();

    List<Vertex> vertices = traversal.order().by(orderTraversal, Order.incr).toList();
    assertThat(vertices, contains(
      likeVertex().withTimId("id1"),
      likeVertex().withTimId("id3"),
      likeVertex().withTimId("id2")));
  }

  private static class IntegerParser implements PropertyParser {
    @Override
    public String parse(String value) {
      return value;
    }

    @Override
    public Comparable<?> parseForSort(String value) {
      return Integer.parseInt(value);
    }

  }
}
