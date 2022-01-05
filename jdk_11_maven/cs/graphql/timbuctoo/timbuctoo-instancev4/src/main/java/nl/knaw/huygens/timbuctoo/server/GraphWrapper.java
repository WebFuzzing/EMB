package nl.knaw.huygens.timbuctoo.server;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface GraphWrapper {
  Graph getGraph();

  /**
   * Returns a GraphTraversalSource that is filtered that only contains the latest edges an vertices.
   * @return the filtered graph as GraphTraversalSource
   */
  GraphTraversalSource getLatestState();

  @Deprecated
  GraphTraversal<Vertex, Vertex> getCurrentEntitiesFor(String... entityTypeNames);
}
