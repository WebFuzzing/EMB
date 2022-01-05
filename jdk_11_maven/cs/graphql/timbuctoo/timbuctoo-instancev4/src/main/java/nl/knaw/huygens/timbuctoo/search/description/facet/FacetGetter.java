package nl.knaw.huygens.timbuctoo.search.description.facet;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Map;
import java.util.Set;

public interface FacetGetter {
  Facet getFacet(String facetName, Map<String, Set<Vertex>> values);
}
