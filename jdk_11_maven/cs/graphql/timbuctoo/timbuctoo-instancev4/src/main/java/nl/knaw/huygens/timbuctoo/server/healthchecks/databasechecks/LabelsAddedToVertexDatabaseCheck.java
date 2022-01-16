package nl.knaw.huygens.timbuctoo.server.healthchecks.databasechecks;

import com.google.common.collect.Sets;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.server.healthchecks.DatabaseCheck;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ElementValidationResult;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ValidationResult;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getEntityTypes;
import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getProp;

public class LabelsAddedToVertexDatabaseCheck implements DatabaseCheck {
  @Override
  public void init(Graph graph, GraphDatabaseService service) {

  }

  @Override
  public ValidationResult check(Vertex vertex) {
    if (!(vertex instanceof Neo4jVertex)) {
      return new ElementValidationResult(false, "Is not a Neo4jVertex.");
    }
    if (vertex.property("types").isPresent()) {
      Set<String> types = new HashSet<>(Arrays.asList(getEntityTypes(vertex)
              .orElseGet(() -> Try.success(new String[0]))
              .getOrElse(() -> new String[0])));

      Sets.SetView<String> difference = Sets.difference(types, ((Neo4jVertex) vertex).labels());

      if (!difference.isEmpty()) {
        return new ElementValidationResult(
          false,
          String.format("Vertex with tim_id %s misses labels %s\n",
            getProp(vertex, "tim_id", String.class).orElse("<UNKNOWN>"),
            difference
          )
        );
      }

    }
    return new ElementValidationResult(true,
      String.format("Vertex with tim_id %s is valid.",
        getProp(vertex, "tim_id", String.class).orElse("<UNKNOWN>"))
    );
  }

  @Override
  public void finish() {

  }
}
