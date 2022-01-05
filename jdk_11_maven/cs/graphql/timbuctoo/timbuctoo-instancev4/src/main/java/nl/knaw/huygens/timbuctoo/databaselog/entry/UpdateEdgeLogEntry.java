package nl.knaw.huygens.timbuctoo.databaselog.entry;

import nl.knaw.huygens.timbuctoo.databaselog.LogOutput;
import nl.knaw.huygens.timbuctoo.databaselog.LogEntry;
import org.apache.tinkerpop.gremlin.structure.Edge;

import java.util.Set;

class UpdateEdgeLogEntry implements LogEntry {
  private final Edge edge;
  private final Edge prevEdge;
  private final PropertyUpdater propertyUpdater;

  public UpdateEdgeLogEntry(Edge edge, Edge prevEdge) {
    this(edge, prevEdge, LogEntry.SYSTEM_PROPERTIES);

  }

  UpdateEdgeLogEntry(Edge edge, Edge prevEdge, Set<String> propertiesToIgnore) {
    this.edge = edge;
    this.prevEdge = prevEdge;
    this.propertyUpdater = new PropertyUpdater(edge, prevEdge, propertiesToIgnore);
  }

  @Override
  public void appendToLog(LogOutput dbLog) {
    dbLog.updateEdge(edge);
    propertyUpdater.updateProperties(dbLog);
  }

}
