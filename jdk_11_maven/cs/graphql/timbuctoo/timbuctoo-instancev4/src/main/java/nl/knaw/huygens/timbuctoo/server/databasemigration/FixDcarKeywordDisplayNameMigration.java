package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.model.properties.LocalProperty;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FixDcarKeywordDisplayNameMigration implements DatabaseMigration {
  private static final Logger LOG = LoggerFactory.getLogger(FixDcarKeywordDisplayNameMigration.class);

  @Override
  public void execute(TinkerPopGraphManager graphWrapper) throws IOException {
    final Graph graph = graphWrapper.getGraph();
    final GraphTraversal<Vertex, Vertex> dcarDisplayNameT = graph.traversal().V()
      .has(T.label, LabelP.of(Vre.DATABASE_LABEL))
      .has("name", "DutchCaribbean")
      .out("hasCollection")
      .has("collectionName", "dcarkeywords")
      .out("hasDisplayName");


    // Only apply this config change if this config actually exists in the current database
    if (dcarDisplayNameT.hasNext()) {
      LOG.info("Changing displayName configuration for dcarkeywords to dcarkeyword_value");
      final Vertex dcarDisplayName = dcarDisplayNameT.next();
      final Transaction transaction = graph.tx();
      if (!transaction.isOpen()) {
        transaction.open();
      }
      dcarDisplayName.property(LocalProperty.DATABASE_PROPERTY_NAME, "dcarkeyword_value");
      transaction.commit();
      transaction.close();
    } else {
      LOG.info("Skipping change for displayName configuration of dcarkeywords " +
        "as this collection does not exist in this database");
    }

  }
}
