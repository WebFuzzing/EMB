package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class DatabaseMigrator {
  private static final Logger LOG = LoggerFactory.getLogger(DatabaseMigrator.class);
  //FIXME: move migrator to database package and make this private again
  public static final String EXECUTED_MIGRATIONS_TYPE = "executed-migrations";
  private final TinkerPopGraphManager graphWrapper;
  private final Map<String, DatabaseMigration> migrations;

  public DatabaseMigrator(TinkerPopGraphManager graphWrapper, Map<String, DatabaseMigration> migrations) {
    this.graphWrapper = graphWrapper;
    this.migrations = migrations;
  }

  public void execute() {
    Graph graph = graphWrapper.getGraph();
    if (graph.tx().isOpen()) {
      LOG.error("Transaction wasn't closed before calling migrator");
    }
    boolean verticesAvailable;
    List<String> executedMigrations;
    try (Transaction transaction = graph.tx()) {
      transaction.open();
      GraphTraversalSource traversalSource = graph.traversal();
      executedMigrations = traversalSource.V()
        .has("type", EXECUTED_MIGRATIONS_TYPE)
        .map(vertexTraverser -> (String) vertexTraverser.get()
          .property("name")
          .value())
        .toList();

      verticesAvailable = traversalSource.V().hasNext();
    }
    migrations.forEach((name, migration) -> {
      try (Transaction transaction = graph.tx()) {
        transaction.open();
        if (!verticesAvailable) {
          LOG.info("Skipping migration with name '{}' because this is a clean database. ", name);
          this.saveExecution(graph, name);
          transaction.commit();
        } else if (!executedMigrations.contains(name)) {
          LOG.info("Executing migration with name '{}'", name);
          try {
            migration.execute(graphWrapper);
            this.saveExecution(graph, name);
            transaction.commit();
          } catch (IOException e) {
            LOG.error("Could not complete migration with name '{}'", name);
          }
        } else {
          LOG.info("Skipping migration with name '{}', because it has already been executed.", name);
        }
      }
    });
  }

  private void saveExecution(Graph graph, String name) {
    Vertex migrationVertex = graph.addVertex();
    migrationVertex.property("type", EXECUTED_MIGRATIONS_TYPE);
    migrationVertex.property("name", name);
    migrationVertex.property("tim_id", UUID.randomUUID().toString());
  }
}
