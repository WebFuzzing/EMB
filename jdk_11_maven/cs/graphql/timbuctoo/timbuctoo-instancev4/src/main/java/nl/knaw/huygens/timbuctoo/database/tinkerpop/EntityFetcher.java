package nl.knaw.huygens.timbuctoo.database.tinkerpop;


import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.UUID;

public interface EntityFetcher {

  GraphTraversal<Vertex, Vertex> getEntity(GraphTraversalSource source, UUID id, Integer rev, String collectionName);
}
