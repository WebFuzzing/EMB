package nl.knaw.huygens.timbuctoo.databaselog;

import nl.knaw.huygens.timbuctoo.database.tinkerpop.EdgeManipulator;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the use case to move all the history to a log file and keep only the latest version of the
 * data as the usable data set. Because the database is missing some revisions of Vertices and Edges, the log will
 * not be usable for restoring a former state. This class will add a placeholder for the missing Vertices and  Edges.
 */
public class DatabaseFixer {
  private final GraphWrapper graphWrapper;

  public DatabaseFixer(GraphWrapper graphWrapper) {
    this.graphWrapper = graphWrapper;
  }

  public void fix() {
    Graph graph = graphWrapper
      .getGraph();
    fixEdges(graph);
    fixVertices(graph);
  }

  private void fixVertices(Graph graph) {
    graph.traversal()
         .V()
         .has("isLatest", true)
         .has("rev")
         .forEachRemaining(this::addMissingVertexVersions);
  }

  private void addMissingVertexVersions(Vertex vertex) {
    int rev = vertex.value("rev");
    if (rev > 1) {
      Iterator<Vertex> previousVersions = vertex.vertices(Direction.IN, "VERSION_OF");
      if (previousVersions.hasNext()) {
        addMissingVertexVersions(previousVersions.next());
      } else {
        Vertex duplicate = vertex.graph().addVertex();
        duplicate.addEdge("VERSION_OF", vertex);
        for (Iterator<VertexProperty<Object>> properties = vertex.properties(); properties.hasNext(); ) {
          VertexProperty<Object> property = properties.next();
          if (Objects.equals(property.key(), "isLatest")) {
            duplicate.property(property.key(), false);
          } else if (Objects.equals(property.key(), "rev")) {
            duplicate.property(property.key(), rev - 1);
          } else if (Objects.equals(property.key(), "modified")) {
            duplicate.property("modified", vertex.value("created"));
          } else {
            duplicate.property(property.key(), property.value());
          }
        }
        addMissingVertexVersions(duplicate);
      }
    }
  }

  private void fixEdges(Graph graph) {
    graph.traversal()
         .E()
         .has("isLatest", true)
         .has("rev")
         .forEachRemaining(this::addMissingEdgeVersions);
  }

  private void addMissingEdgeVersions(Edge edge) {
    int rev = edge.value("rev");
    if (rev > 1) {
      Optional<Edge> previousVersion = getPreviousVersion(edge);
      if (previousVersion.isPresent()) {
        addMissingEdgeVersions(previousVersion.get());
      } else {
        // FIXME stop using EdgeManipulator
        Edge duplicate = EdgeManipulator.duplicateEdge(edge);
        for (Iterator<Property<Object>> properties = edge.properties(); properties.hasNext(); ) {
          Property<Object> property = properties.next();
          if (Objects.equals(property.key(), "isLatest")) {
            duplicate.property("isLatest", false);
          } else if (Objects.equals(property.key(), "rev")) {
            duplicate.property("rev", rev - 1);
          } else if (Objects.equals(property.key(), "modified")) {
            duplicate.property("modified", edge.value("created"));
          } else if (property.key().endsWith("_accepted")) {
            duplicate.property(property.key(), true);
          }
        }
        addMissingEdgeVersions(duplicate);
      }
    }
  }

  public Optional<Edge> getPreviousVersion(Edge edge) {
    int rev = edge.value("rev");
    Optional<Edge> prev = Optional.empty();
    for (Iterator<Edge> edges = edge.outVertex().edges(Direction.OUT, edge.label()); edges.hasNext(); ) {
      Edge next = edges.next();
      if (next.<Integer>value("rev") == (rev - 1) && Objects.equals(next.inVertex().id(), edge.inVertex().id())) {
        prev = Optional.of(next);
      }
    }

    return prev;
  }

}
