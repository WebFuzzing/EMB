package nl.knaw.huygens.timbuctoo.databaselog.entry;

import nl.knaw.huygens.timbuctoo.databaselog.LogEntry;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Iterator;

/**
 * A class to create instances of EdgeLogEntry and VertexLogEntry, but hides the details if the LogEntry is a Create
 * or Update.
 */
public class LogEntryFactory {

  private final EdgeRetriever edgeRetriever;

  public LogEntryFactory() {
    edgeRetriever = new EdgeRetriever();
  }

  public LogEntry createForVertex(Vertex vertex) {
    Iterator<Vertex> previous = vertex.vertices(Direction.IN, "VERSION_OF");
    if (previous.hasNext()) {
      return new UpdateVertexLogEntry(vertex, previous.next());
    }
    return new CreateVertexLogEntry(vertex);
  }

  public LogEntry createForEdge(Edge edge) throws IllegalArgumentException {
    Property<Integer> revProp = edge.property("rev");
    if (!revProp.isPresent()) {
      String id = edge.value("tim_id");
      throw new IllegalArgumentException(
        String.format("Edge with id '%s' has no property 'rev'. This edge will be ignored.", id)
      );
    }
    Integer rev = revProp.value();

    if (rev > 1) {
      Edge prevEdge = edgeRetriever.getPreviousVersion(edge);

      return new UpdateEdgeLogEntry(edge, prevEdge);
    }
    return new CreateEdgeLogEntry(edge);

  }

}
