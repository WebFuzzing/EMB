package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.ListFacetGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.LocalPropertyValueGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListFacetDescription extends AbstractFacetDescription {

  private final PropertyParser parser;

  public ListFacetDescription(String facetName, String propertyName, PropertyParser parser) {
    super(facetName, propertyName, new ListFacetGetter(parser), new LocalPropertyValueGetter());
    this.parser = parser;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    Optional<FacetValue> facetValue =
            facets.stream().filter(facet -> Objects.equals(facet.getName(), facetName)).findFirst();

    if (facetValue.isPresent()) {
      FacetValue value = facetValue.get();
      if (value instanceof ListFacetValue) {

        List<String> values = ((ListFacetValue) value).getValues();
        if (!values.isEmpty()) {
          graphTraversal.where(__.has(propertyName, P.test((o1, o2) -> {
            List<String> possibileValues = (List<String>) o2;
            return possibileValues.contains(parser.parse("" + o1));
          }, values)));
        }
      }
    }
  }
}
