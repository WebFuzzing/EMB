package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.UUID;


public class GremlinEntityFetcher implements EntityFetcher {
  @Override
  public GraphTraversal<Vertex, Vertex> getEntity(GraphTraversalSource source, UUID id, Integer rev,
                                                  String collectionName) {
    if (rev == null) {
      return source
        .V()
        .has("tim_id", id.toString())
        .not(__.has("deleted", true))
        .has("isLatest", true);
    }
    return source
      .V()
      .has("tim_id", id.toString())
      .has("rev", rev)
      .not(__.has("deleted", true))
      .has("isLatest", false);
  }
}
