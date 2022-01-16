package nl.knaw.huygens.timbuctoo.server.healthchecks;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;

public interface DatabaseCheck {
  void init(Graph graph, GraphDatabaseService service);

  ValidationResult check(Vertex vertex);

  void finish();
}
