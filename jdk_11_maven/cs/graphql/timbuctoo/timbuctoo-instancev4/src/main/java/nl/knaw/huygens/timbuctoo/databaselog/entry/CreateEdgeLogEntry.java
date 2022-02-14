package nl.knaw.huygens.timbuctoo.databaselog.entry;

import nl.knaw.huygens.timbuctoo.databaselog.LogOutput;
import nl.knaw.huygens.timbuctoo.databaselog.LogEntry;
import nl.knaw.huygens.timbuctoo.util.StreamIterator;
import org.apache.tinkerpop.gremlin.structure.Edge;

import java.util.Set;

class CreateEdgeLogEntry implements LogEntry {
  private final Edge edge;
  private final Set<String> propertiesToIgnore;

  public CreateEdgeLogEntry(Edge edge) {
    this(edge, LogEntry.SYSTEM_PROPERTIES);
  }

  public CreateEdgeLogEntry(Edge edge, Set<String> propertiesToIgnore) {
    this.edge = edge;
    this.propertiesToIgnore = propertiesToIgnore;
  }

  @Override
  public void appendToLog(LogOutput dbLog) {
    dbLog.newEdge(edge);

    StreamIterator.stream(edge.properties())
                  .filter(property -> !propertiesToIgnore.contains(property.key()))
                  .forEach(dbLog::newProperty);
  }
}
