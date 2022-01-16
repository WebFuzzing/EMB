package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.ListFacetGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AltNameFacetDescription extends AbstractFacetDescription {
  public static final Logger LOG = LoggerFactory.getLogger(AltNameFacetDescription.class);
  private final String propertyName;
  private final ObjectMapper objectMapper;

  public AltNameFacetDescription(String facetName, String propertyName) {
    super(facetName, propertyName, new ListFacetGetter(), null);
    this.facetName = facetName;
    this.propertyName = propertyName;
    objectMapper = new ObjectMapper();
  }

  @Override
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facetValues) {
    Optional<FacetValue> first = facetValues.stream()
                                            .filter(facetValue -> Objects.equals(facetValue.getName(), facetName))
                                            .findFirst();

    if (!first.isPresent()) {
      return;
    }

    FacetValue facetValue = first.get();

    if (!(facetValue instanceof ListFacetValue)) {
      return;
    }

    List<String> values = ((ListFacetValue) facetValue).getValues();
    if (values.isEmpty()) {
      return;
    }

    graphTraversal
      .where(__.<String>has(propertyName, P.test((o1, o2) -> o1 instanceof String && o2 instanceof List &&
        ((List<?>) o2).stream().anyMatch(value -> ((String) o1).contains("\"" + value + "\"")), values)));
  }

  @Override
  public List<String> getValues(Vertex vertex) {
    List<String> values = Lists.newArrayList();
    VertexProperty<String> property = vertex.property(propertyName);
    if (property.isPresent()) {
      String value = property.value();
      try {
        AltNames altNames = objectMapper.readValue(value, AltNames.class);

        altNames.list.forEach(altName -> values.add(altName.getDisplayName()));
      } catch (IOException e) {
        LOG.error("Could not convert '{}'", value);
        LOG.error("Exception throw.", e);
      }
    }

    return values;
  }
}
