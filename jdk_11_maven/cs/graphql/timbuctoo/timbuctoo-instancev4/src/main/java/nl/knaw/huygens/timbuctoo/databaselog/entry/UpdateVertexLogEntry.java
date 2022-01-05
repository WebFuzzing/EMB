package nl.knaw.huygens.timbuctoo.databaselog.entry;

import nl.knaw.huygens.timbuctoo.databaselog.LogOutput;
import nl.knaw.huygens.timbuctoo.databaselog.LogEntry;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Set;

class UpdateVertexLogEntry implements LogEntry {
  private final Vertex vertex;
  private final Vertex previous;
  private final PropertyUpdater propertyUpdater;


  public UpdateVertexLogEntry(Vertex vertex, Vertex previousVersion) {
    this(vertex, previousVersion, LogEntry.SYSTEM_PROPERTIES);
  }

  UpdateVertexLogEntry(Vertex vertex, Vertex previousVersion, Set<String> propertiesToIgnore) {
    this.vertex = vertex;
    this.previous = previousVersion;
    this.propertyUpdater = new PropertyUpdater(vertex, previousVersion, propertiesToIgnore);
  }

  @Override
  public void appendToLog(LogOutput dbLog) {
    dbLog.updateVertex(vertex);
    propertyUpdater.updateProperties(dbLog);
  }
}
