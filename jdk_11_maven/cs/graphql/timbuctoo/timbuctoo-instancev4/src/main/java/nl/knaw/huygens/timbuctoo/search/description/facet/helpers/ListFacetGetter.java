package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;


import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetGetter;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ListFacetGetter implements FacetGetter {

  private final PropertyParser parser;

  public ListFacetGetter() {
    this.parser = null;
  }

  public ListFacetGetter(PropertyParser parser) {
    this.parser = parser;
  }

  private String parseValue(String rawValue) {
    return parser == null ? rawValue : parser.parse(rawValue);
  }

  @Override
  public Facet getFacet(String facetName, Map<String, Set<Vertex>> values) {
    List<Facet.Option> options = values.entrySet().stream()
            .map(entry -> new Facet.DefaultOption(parseValue(entry.getKey()), entry.getValue().size()))
            .filter(facetOption -> facetOption.getName() != null)
            .collect(toList());

    return new Facet(facetName, options, "LIST");
  }
}
