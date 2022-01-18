package nl.knaw.huygens.timbuctoo.search.description;

import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FacetDescription {

  String getName();

  Facet getFacet(Map<String, Set<Vertex>> values);

  /**
   * Adds a filter to the graphTraversal if the searchRequest contains a facet for the description.
   */
  void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets);

  List<String> getValues(Vertex vertex);

}
