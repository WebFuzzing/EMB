package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.ListFacetGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

class CharterPortaalFondsFacetDescription extends AbstractFacetDescription {
  public static final Logger LOG = LoggerFactory.getLogger(CharterPortaalFondsFacetDescription.class);
  public static final String FONDS_NAAM = "charterdocument_fondsNaam";
  public static final String FONDS = "charterdocument_fonds";

  public CharterPortaalFondsFacetDescription(String facetName, PropertyParser propertyParser) {
    super(facetName, null, new ListFacetGetter(propertyParser), null);
  }

  @Override
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    Optional<FacetValue> first =
      facets.stream().filter(facetValue -> Objects.equals(facetValue.getName(), getName())).findFirst();

    if (first.isPresent()) {
      FacetValue facetValue = first.get();
      if (facetValue instanceof ListFacetValue) {
        ListFacetValue listFacetValue = (ListFacetValue) facetValue;
        List<String> values = listFacetValue.getValues();
        if (values.isEmpty()) {
          return;
        }

        List<String> formattedVals = values.stream()
                                           .map(val -> val.substring(val.indexOf("(") + 1, val.length() - 1))
                                           .collect(toList());
        graphTraversal.where(__.has(FONDS, P.within(formattedVals)));
      } else {
        LOG.error("Facet with name '{}' is not a ListFacet", getName());
      }
    }
  }

  private String createFacetValue(Object fondsNaam, Object fonds) {
    return String.format("%s (%s)", fondsNaam, fonds);
  }

  @Override
  public List<String> getValues(Vertex vertex) {
    VertexProperty<String> fondsNaamProp = vertex.property(FONDS_NAAM);
    String fondsNaam = fondsNaamProp.isPresent() ? fondsNaamProp.value() : "";

    VertexProperty<String> fondsProp = vertex.property(FONDS);
    String fonds = fondsNaamProp.isPresent() ? fondsProp.value() : "";

    return Lists.newArrayList(createFacetValue(fondsNaam, fonds));

  }
}
