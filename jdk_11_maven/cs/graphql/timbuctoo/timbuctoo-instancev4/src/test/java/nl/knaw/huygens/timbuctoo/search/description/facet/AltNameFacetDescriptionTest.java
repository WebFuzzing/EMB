package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.AltName;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AltNameFacetDescriptionTest {

  public static final String PROPERTY_NAME = "propertyName";

  @Test
  public void getValuesReturnsTheDisplayNamesOfAllTheAltNames() throws Exception {
    String displayName1 = "displayName1";
    String displayName2 = "displayName2";
    AltNames altNames = new AltNames();
    altNames.list = Lists.newArrayList(new AltName("nameType", displayName1), new AltName("nameType", displayName2));

    String altNamesPropValue = new ObjectMapper().writeValueAsString(altNames);
    Vertex vertex = vertex().withProperty(PROPERTY_NAME, altNamesPropValue).build();
    AltNameFacetDescription instance = new AltNameFacetDescription("facetName", PROPERTY_NAME);

    List<String> values = instance.getValues(vertex);

    assertThat(values, containsInAnyOrder(displayName1, displayName2));
  }

  @Test
  public void getValuesReturnsAnEmptyListIfTheVertexDoesNotContainTheProperty() {
    Vertex vertex = vertex().build();

    AltNameFacetDescription instance = new AltNameFacetDescription("facetName", "propertyName");

    List<String> values = instance.getValues(vertex);

    assertThat(values, is(empty()));
  }

}
