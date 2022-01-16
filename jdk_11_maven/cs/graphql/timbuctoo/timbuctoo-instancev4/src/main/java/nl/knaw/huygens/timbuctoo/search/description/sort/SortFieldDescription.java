package nl.knaw.huygens.timbuctoo.search.description.sort;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;

import java.util.List;

public interface SortFieldDescription {
  String getName();

  List<GraphTraversal<Object, Object>> getTraversal();
}
