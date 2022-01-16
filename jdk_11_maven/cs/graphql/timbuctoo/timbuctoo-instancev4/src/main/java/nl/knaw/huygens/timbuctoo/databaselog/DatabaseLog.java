package nl.knaw.huygens.timbuctoo.databaselog;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.databaselog.entry.LogEntryFactory;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Currently (as of 2016-06-13) the Timbuctoo database contains all versions of all entities and all versions of all
 * relations as one interlinked graph. The old versions are filtered out while querying. We want to simplify the
 * database model and the code that uses the database directly, without losing information. Therefore we're moving the
 * history to a separate logOutput.
 * </p>
 * <p>
 * This class creates that log from the current format.
 * </p>
 * <p>
 * It sorts all vertices (i.e. versions of entities) as one big chronological list and then iterates over it, creating
 * the log entries and calling their addToLog method. //FIXME an object that is immediately removed?
 * </p>
 * <p>
 * A complicating factor is that many vertices and edges share the exact same modified time. So we don't know the exact
 * order in which they were added. For vertices we do not to care about the exact order, however an edge between two
 * vertices can only be added once those vertices exist (obviously).
 * </p>
 * <p>
 * Our solution is: for a given modified-time we first add {@link LogEntry}s for all vertices and then for all edges.
 * </p>
 * <p>
 * After creating the {@link LogEntry} objects it calls the {@link LogOutput} to write the {@link LogEntry}s. At this
 * moment they are written as text to a file, we will later replace this with a {@link LogOutput} implementation that
 * creates log vertices in the database.
 * </p>
 */
public class DatabaseLog {
  public static final Logger LOG = LoggerFactory.getLogger(DatabaseLog.class);
  private final GraphWrapper graphWrapper;
  private final LogEntryFactory logEntryFactory;
  private final LogOutput logOutput;
  private final ObjectMapper objectMapper;

  public DatabaseLog(GraphWrapper graphWrapper) {
    this(graphWrapper, new LogEntryFactory());
  }

  DatabaseLog(GraphWrapper graphWrapper, LogEntryFactory logEntryFactory) {
    this(graphWrapper, logEntryFactory, new GraphLogOutput(graphWrapper));
  }

  DatabaseLog(GraphWrapper graphWrapper, LogEntryFactory logEntryFactory, LogOutput logOutput) {
    this.graphWrapper = graphWrapper;
    this.logEntryFactory = logEntryFactory;
    this.logOutput = logOutput;
    this.objectMapper = new ObjectMapper();
  }

  public void generate() {
    long prevTimestamp = 0L;

    @SuppressWarnings("unchecked") // union warns about unchecked.
      GraphTraversal<Vertex, ? extends Element> results = graphWrapper
      .getGraph()
      .traversal()
      .V().has("modified").has("rev").has("tim_id")
      .not(__.has(T.label, LabelP.of("searchresult"))) // ignore search results
      .not(__.bothE("NEXT_ITEM")) // ignore database log items
      .union(__.identity(), __.outE().has("modified"))
      /* add each version once, , usually the last version consists of 2 Edges / Vertices, one with isLatest on true and
       * a copy.
       */
      .dedup().by(__.valueMap("rev", "tim_id"))
      .order()
      .by("modified", new ChangeStringComparator());

    List<Edge> edges = Lists.newArrayList();
    List<Vertex> vertices = Lists.newArrayList();
    logOutput.prepareToWrite();
    for (; results.hasNext(); ) {
      Element element = results.next();
      String modifiedString = element.value("modified");

      try {
        long curTimestamp = getTimestampFromChangeString(modifiedString);
        /* To make sure the EdgeLogEntries are added right behind the VertexLogEntries with the same timestamp, add
         * the LogEntries to the database before the timestamp changes.
         */
        if (prevTimestamp != curTimestamp) {
          vertices.forEach(vertex -> logEntryFactory.createForVertex(vertex).appendToLog(logOutput));
          vertices.clear();
          edges.forEach(edge -> logEntryFactory.createForEdge(edge).appendToLog(logOutput));
          edges.clear();
          prevTimestamp = curTimestamp;
        }

        if (element instanceof Vertex) {
          vertices.add((Vertex) element);
        } else if (element instanceof Edge) {
          edges.add((Edge) element);
        } else {
          LOG.error("Element type {} not expected.", element.getClass());
        }

      } catch (IOException e) {
        LOG.error("Change '{}' could nog be parsed.", modifiedString);
      }
    }
    // add remaining
    vertices.forEach(vertex -> logEntryFactory.createForVertex(vertex).appendToLog(logOutput));
    vertices.clear();
    edges.forEach(edge -> logEntryFactory.createForEdge(edge).appendToLog(logOutput));
    edges.clear();
    logOutput.finishWriting();
  }

  private long getTimestampFromChangeString(String changeString) throws IOException {
    return objectMapper.readValue(changeString, Change.class).getTimeStamp();
  }
}
