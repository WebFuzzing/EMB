package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.ListFacetGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class EdgeListFacetDescription extends AbstractFacetDescription {

  private final List<String> relationNames;

  public EdgeListFacetDescription(String facetName, String... relationNames) {
    super(facetName, null, new ListFacetGetter(), null);
    this.relationNames = Arrays.asList(relationNames);
  }

  @Override
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    Optional<FacetValue> facetValue =
            facets.stream().filter(facet -> Objects.equals(facet.getName(), facetName)).findFirst();

    if (facetValue.isPresent()) {
      FacetValue value = facetValue.get();
      if (value instanceof ListFacetValue) {

        List<String> values = ((ListFacetValue) value).getValues();

        if (!values.isEmpty()) {
          graphTraversal.where(__.inE(values.toArray(new String[values.size()])));
        }
      }
    }
  }

  public List<String> getValues(Vertex vertex) {
    return this.relationNames.stream().filter(relationName ->
      vertex.edges(Direction.IN, relationName).hasNext()
    ).collect(toList());
  }
}
