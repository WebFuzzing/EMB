package nl.knaw.huygens.timbuctoo.database.tinkerpop;


import nl.knaw.huygens.timbuctoo.logging.Logmarkers;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class Neo4jLuceneEntityFetcher extends GremlinEntityFetcher {
  public static final int MAX_VERSION_OF_DEPTH = 100;
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Neo4jLuceneEntityFetcher.class);
  private final GraphDatabaseService graphDatabase;
  private final IndexHandler indexHandler;

  public Neo4jLuceneEntityFetcher(TinkerPopGraphManager graphManager, IndexHandler indexHandler) {
    this.graphDatabase = graphManager.getGraphDatabase();
    this.indexHandler = indexHandler;
  }

  public GraphTraversal<Vertex, Vertex> getEntity(GraphTraversalSource source, UUID id, Integer rev,
                                                  String collectionName) {
    if (rev == null) {
      Optional<Vertex> foundVertex = getVertexByIndex(source, id, collectionName);

      if (foundVertex.isPresent()) {
        return source.V(foundVertex.get().id());
      }
    }
    return super.getEntity(source, id, rev, collectionName);
  }

  private Optional<Vertex> getVertexByIndex(GraphTraversalSource source, UUID id, String collectionName) {
    // Look up the vertex for this neo4j Node
    Optional<Vertex> vertexOpt = indexHandler.findById(id);

    // Return if the neo4j Node ID matches no vertex (extreme edge case)
    if (!vertexOpt.isPresent()) {
      LOG.error(Logmarkers.databaseInvariant,
        "Vertex with tim_id {} is found in index with id {}L but not in graph database", id);
      return Optional.empty();
    }

    // Get the latest version of the found Vertex
    Vertex foundVertex = vertexOpt.get();
    int infinityGuard = 0;
    while (foundVertex.vertices(Direction.OUT, "VERSION_OF").hasNext()) {
      // The neo4j index Node is one version_of behind the actual node
      foundVertex = foundVertex.vertices(Direction.OUT, "VERSION_OF").next();
      if (++infinityGuard >= MAX_VERSION_OF_DEPTH) {
        LOG.error(Logmarkers.databaseInvariant, "Vertices with tim_id {} might have circular VERSION_OF", id);
        return Optional.empty();
      }
    }

    // Only if this latest version is truly registered as latest return this as a successful hit
    if (foundVertex.value("isLatest")) {
      return Optional.of(foundVertex);
    } else {
      LOG.error(Logmarkers.databaseInvariant,
        "Last version of vertex with tim_id {} is not marked as isLatest=true", id);
    }

    // Failed to find vertex in lucene index, so return
    return Optional.empty();
  }
}
