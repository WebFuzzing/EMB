package nl.knaw.huygens.timbuctoo.databaselog;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Set;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;

/**
 * This class validates if the DatabaseLog has created a log entry for each domain Vertex and Edge.
 */
public class GraphLogValidator {
  public static final Logger LOG = LoggerFactory.getLogger(GraphLogValidator.class);
  private final GraphWrapper graphWrapper;

  public GraphLogValidator(GraphWrapper graphWrapper) {
    this.graphWrapper = graphWrapper;
  }

  @SuppressWarnings("unchecked")
  public void writeReport(Writer writer) {
    LOG.info("Start Vertex validation");
    Graph graph = graphWrapper.getGraph();

    Set<ValidationElement> vertexIdsAndVersions = graph.traversal().V()
                                                       // ignore non domain vertices
                                                       .not(has(T.label, LabelP.of("searchresult")))
                                                       .not(has(T.label, LabelP.of("createEdgeEntry")))
                                                       .not(has(T.label, LabelP.of("updateEdgeEntry")))
                                                       .not(has(T.label, LabelP.of("createVertexEntry")))
                                                       .not(has(T.label, LabelP.of("updateVertexEntry")))
                                                       // ignore the corrupt vertices
                                                       .has("tim_id")
                                                       .has("rev")
                                                       .map(v -> new ValidationElement(
                                                         "Vertex",
                                                         v.get().id(),
                                                         v.get().value("tim_id"),
                                                         v.get().value("rev")))
                                                       .toSet();

    Set<ValidationElement> vertexLogEntryIdsAndVersions = graph.traversal().V()
                                                               .or(has(T.label, LabelP.of("createVertexEntry")),
                                                                 has(T.label, LabelP.of("updateVertexEntry"))
                                                               )
                                                               .map(v -> new ValidationElement(
                                                                 "Vertex",
                                                                 v.get().id(),
                                                                 v.get().value("TIM_tim_id"),
                                                                 v.get().value("rev")))
                                                               .toSet();

    Set<ValidationElement> missingVertexLogEntries =
      Sets.difference(vertexIdsAndVersions, vertexLogEntryIdsAndVersions);
    missingVertexLogEntries.forEach(el -> el.writeLogString(writer));


    LOG.info("Start Edge validation");
    Set<ValidationElement> edgeIdsAndVersions = graph.traversal().E()
                                                     // ignore system edges
                                                     .not(__.hasLabel("VERSION_OF", "NEXT_ITEM"))
                                                     // ignore the corrupt edges
                                                     .has("tim_id")
                                                     .has("rev")
                                                     .map(e -> new ValidationElement(
                                                       "Edge",
                                                       e.get().id(),
                                                       e.get().value("tim_id"),
                                                       e.get().value("rev")))
                                                     .toSet();

    Set<ValidationElement> edgeLogEntryIdsAndVersions = graph.traversal().V()
                                                             .or(has(T.label, LabelP.of("createEdgeEntry")),
                                                               has(T.label, LabelP.of("updateEdgeEntry"))
                                                             )
                                                             .map(e -> new ValidationElement(
                                                               "Edge",
                                                               e.get().id(),
                                                               e.get().value("TIM_tim_id"),
                                                               e.get().value("rev")))
                                                             .toSet();

    Set<ValidationElement> missingLogEntries = Sets.difference(edgeIdsAndVersions, edgeLogEntryIdsAndVersions);
    missingLogEntries.forEach(el -> el.writeLogString(writer));
  }

  private static class ValidationElement {
    public static final ArrayList<String> EXCLUDE_FIELDS = Lists.newArrayList("id", "type");
    private final String type;
    private final Object id;
    private final Object timId;
    private final Object rev;

    private ValidationElement(String type, Object id, Object timId, Object rev) {
      this.type = type;
      this.id = id;
      this.timId = timId;
      this.rev = rev;
    }

    public void writeLogString(Writer writer) {
      String logString = String
        .format("%s with id '%s' and tim_id '%s' and rev '%s' has no log entry.%n", type, id, timId, rev);
      try {
        LOG.debug("write \"{}\"", logString);
        writer.write(logString);
      } catch (IOException e) {
        LOG.error("Cannot write log string \"{}\"", logString);
      }
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj, EXCLUDE_FIELDS);
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this, EXCLUDE_FIELDS);
    }
  }
}
