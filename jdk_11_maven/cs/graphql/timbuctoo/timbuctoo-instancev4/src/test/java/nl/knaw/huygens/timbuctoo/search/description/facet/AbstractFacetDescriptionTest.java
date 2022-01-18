package nl.knaw.huygens.timbuctoo.search.description.facet;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.FacetDescription;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AbstractFacetDescriptionTest {

  private static final String FACET_NAME = "facetName";
  private static final String PROPERTY_NAME = "propertyName";
  private static final String PROP_VAL = "propertyValue";

  private class FacetDescriptionImpl extends AbstractFacetDescription {

    public FacetDescriptionImpl(String facetName, String propertyName, FacetGetter facetGetter,
                                PropertyValueGetter propertyValueGetter) {
      super(facetName, propertyName, facetGetter, propertyValueGetter);
    }

    @Override
    public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
      return;
    }
  }

  @Test
  public void getNameReturnsTheFacetName() {
    PropertyValueGetter propertyValueGetter = mock(PropertyValueGetter.class);
    FacetGetter facetGetter = mock(FacetGetter.class);
    FacetDescription instance = new FacetDescriptionImpl(FACET_NAME, PROPERTY_NAME, facetGetter, propertyValueGetter);

    assertThat(instance.getName(), equalTo(FACET_NAME));
  }

  @Test
  public void getFacetInvokesTheFacetGetterToReturnTheFacet() {
    PropertyValueGetter propertyValueGetter = mock(PropertyValueGetter.class);
    FacetGetter facetGetter = mock(FacetGetter.class);
    Facet mockFacet = mock(Facet.class);

    given(facetGetter.getFacet(any(), any())).willReturn(mockFacet);

    FacetDescription instance = new FacetDescriptionImpl(FACET_NAME, PROPERTY_NAME, facetGetter, propertyValueGetter);

    Map<String, Set<Vertex>> facetCounts = new HashMap<>();
    facetCounts.put(PROP_VAL, Sets.newHashSet());

    Facet result = instance.getFacet(facetCounts);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

    assertEquals(result, mockFacet);

    ArgumentCaptor<HashMap> captor1 = ArgumentCaptor.forClass(HashMap.class);
    verify(facetGetter, times(1)).getFacet(captor.capture(), captor1.capture());
    assertThat(captor.getValue(), equalTo(FACET_NAME));
    assertThat(captor1.getValue(), equalTo(facetCounts));
  }

  @Test
  public void getValuesInvokesThePropertyValueGetterToReturnThePropertyValues() {
    PropertyValueGetter propertyValueGetter = mock(PropertyValueGetter.class);
    FacetGetter facetGetter = mock(FacetGetter.class);
    List<String> mockValues = mock(List.class);
    Vertex vertex = mock(Vertex.class);

    given(propertyValueGetter.getValues(any(), any())).willReturn(mockValues);

    FacetDescription instance = new FacetDescriptionImpl(FACET_NAME, PROPERTY_NAME, facetGetter, propertyValueGetter);
    List<String> result = instance.getValues(vertex);

    assertEquals(result, mockValues);

    ArgumentCaptor<Vertex> captor = ArgumentCaptor.forClass(Vertex.class);
    ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
    verify(propertyValueGetter, times(1)).getValues(captor.capture(), captor1.capture());
    assertThat(captor.getValue(), equalTo(vertex));
    assertThat(captor1.getValue(), equalTo(PROPERTY_NAME));
  }
}
