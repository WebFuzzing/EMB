package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class RemoveSearchResultsMigration implements DatabaseMigration {
  private static final Logger LOG = getLogger(RemoveSearchResultsMigration.class);

  @Override
  public void execute(TinkerPopGraphManager graphWrapper) throws IOException {
    Graph graph = graphWrapper.getGraph();
    try (Transaction tx = graph.tx()) {
      graph.traversal().V()
        .forEachRemaining(v -> {
          if (!v.property("types").isPresent()) {
            return;
          }
          Object types = v.value("types");
          if (!(types instanceof String)) {
            LOG.error("Types property that is not a string at " + v.id());
            return;
          }
          if (!((String) types).contains("\"searchresult\"")) {
            return;
          }
          LOG.info("Removing vertex with id " + v.id() + ". types=" + v.value("types"));
          v.remove();
        });
      tx.commit();
    }
  }
}
