package nl.knaw.huygens.timbuctoo.databaselog.entry;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Objects;

/**
 * This is a helper class for the LogEntryFactory to find a previous version of an Edge. When no previous version is
 * found the version will be estimated, with the information available from the current Edge.
 */
class EdgeRetriever {

  public static final Logger LOG = LoggerFactory.getLogger(EdgeRetriever.class);

  public Edge getPreviousVersion(Edge edge) {
    Integer rev = edge.<Integer>value("rev");
    Edge previousVersion = null;
    for (Iterator<Edge> edges = edge.outVertex().edges(Direction.OUT, edge.label()); edges.hasNext(); ) {
      Edge next = edges.next();
      if (next.<Integer>value("rev") == (rev - 1) && Objects.equals(next.inVertex().id(), edge.inVertex().id())) {
        previousVersion = next;
      }
    }

    return previousVersion;
  }

}

