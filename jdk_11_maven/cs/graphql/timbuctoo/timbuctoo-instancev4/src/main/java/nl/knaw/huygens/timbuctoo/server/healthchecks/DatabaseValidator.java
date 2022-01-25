package nl.knaw.huygens.timbuctoo.server.healthchecks;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class DatabaseValidator {
  private final TinkerPopGraphManager graphManager;
  private DatabaseCheck[] databaseChecks;

  public DatabaseValidator(TinkerPopGraphManager graphManager, DatabaseCheck... databaseChecks) {
    this.graphManager = graphManager;
    this.databaseChecks = databaseChecks;
  }

  public ValidationResult check() {
    GraphTraversal<Vertex, Vertex> traversal = graphManager.getGraph().traversal().V();
    List<ValidationResult> validationResults = Lists.newArrayList();
    for (DatabaseCheck databaseCheck : databaseChecks) {
      databaseCheck.init(graphManager.getGraph(), graphManager.getGraphDatabase());
    }
    while (traversal.hasNext()) {
      Vertex vertex = traversal.next();
      for (DatabaseCheck databaseCheck : databaseChecks) {
        validationResults.add(databaseCheck.check(vertex));
      }
    }
    for (DatabaseCheck databaseCheck : databaseChecks) {
      databaseCheck.finish();
    }
    return new CompositeValidationResult(validationResults);
  }

}
