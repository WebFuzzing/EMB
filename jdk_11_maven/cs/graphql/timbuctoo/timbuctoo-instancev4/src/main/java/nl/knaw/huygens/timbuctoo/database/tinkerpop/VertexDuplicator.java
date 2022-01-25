package nl.knaw.huygens.timbuctoo.database.tinkerpop;


import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;
import java.util.UUID;

public class VertexDuplicator {

  public static final String VERSION_OF = "VERSION_OF";
  public static final String IS_LATEST = "isLatest";

  public static void duplicateVertex(Graph graph, Vertex vertex, IndexHandler indexHandler) {
    GraphTraversalSource traversal = graph.traversal();
    duplicateVertex(traversal, vertex, indexHandler);
  }

  public static Vertex duplicateVertex(GraphTraversalSource traversal, Vertex vertex, IndexHandler indexHandler) {
    Vertex duplicate = traversal.addV().next();

    for (Iterator<VertexProperty<Object>> properties = vertex.properties(); properties.hasNext(); ) {
      VertexProperty<Object> property = properties.next();
      duplicate.property(property.key(), property.value());
    }

    for (String label : ((Neo4jVertex) vertex).labels()) {
      ((Neo4jVertex) duplicate).addLabel(label);
    }

    moveIncomingEdges(vertex, duplicate, indexHandler);
    moveOutgoingEdges(vertex, duplicate, indexHandler);

    vertex.property(IS_LATEST, false);
    duplicate.property(IS_LATEST, true);
    vertex.addEdge(VERSION_OF, duplicate);
    return duplicate;
  }

  static void moveOutgoingEdges(Vertex vertex, Vertex duplicate, IndexHandler indexHandler) {
    for (Iterator<Edge> edges = vertex.edges(Direction.OUT); edges.hasNext(); ) {
      Edge edge = edges.next();
      if (edge.label().equals(VERSION_OF)) {
        continue;
      }

      Edge duplicateEdge = duplicate.addEdge(edge.label(), edge.inVertex());

      for (Iterator<Property<Object>> properties = edge.properties(); properties.hasNext(); ) {
        Property<Object> property = properties.next();

        duplicateEdge.property(property.key(), property.value());
      }
      if (duplicateEdge.<Boolean>property("isLatest").orElse(false)) {
        duplicateEdge.<String>property("tim_id")
          .ifPresent(p -> indexHandler.upsertIntoEdgeIdIndex(UUID.fromString(p), duplicateEdge));
      }
      edge.remove();
    }
  }

  static void moveIncomingEdges(Vertex vertex, Vertex duplicate, IndexHandler indexHandler) {
    for (Iterator<Edge> edges = vertex.edges(Direction.IN); edges.hasNext(); ) {
      Edge edge = edges.next();
      if (edge.label().equals(VERSION_OF)) {
        continue;
      }
      Edge duplicateEdge = edge.outVertex().addEdge(edge.label(), duplicate);
      for (Iterator<Property<Object>> properties = edge.properties(); properties.hasNext(); ) {
        Property<Object> property = properties.next();

        duplicateEdge.property(property.key(), property.value());
      }
      if (duplicateEdge.<Boolean>property("isLatest").orElse(false)) {
        duplicateEdge.<String>property("tim_id")
          .ifPresent(p -> indexHandler.upsertIntoEdgeIdIndex(UUID.fromString(p), duplicateEdge));
      }
      edge.remove();
    }
  }
}
