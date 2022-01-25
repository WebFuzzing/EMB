package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.FacetDescription;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.ListFacetGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.MultiValuePropertyGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.RelatedMultiValuePropertyGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.RelatedPropertyValueGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A facet description that creates a "LIST" facet with multi-valued properties from connected vertices.
 */
public class RelatedMultiValueListFacetDescription extends AbstractFacetDescription {

  private final String[] relations;

  public RelatedMultiValueListFacetDescription(String facetName, String propertyName, String... relations) {
    super(facetName, propertyName, new ListFacetGetter(), new RelatedMultiValuePropertyGetter(relations));
    this.relations = relations;
  }

  @Override
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    Optional<FacetValue> value = facets.stream()
            .filter(facetValue -> Objects.equals(facetValue.getName(), facetName))
            .findFirst();

    if (value.isPresent()) {
      FacetValue facetValue = value.get();
      if (facetValue instanceof ListFacetValue) {
        List<String> values = ((ListFacetValue) facetValue).getValues();
        if (!values.isEmpty()) {
          graphTraversal.where(__.bothE(relations).otherV().has(propertyName, P.test((o1, o2) -> {
            return o1 instanceof String && o2 instanceof List &&
                    ((List<?>) o2).stream().anyMatch(val -> ((String) o1).contains("\"" + val + "\""));
          }, values)));
        }
      }
    }
  }
}
