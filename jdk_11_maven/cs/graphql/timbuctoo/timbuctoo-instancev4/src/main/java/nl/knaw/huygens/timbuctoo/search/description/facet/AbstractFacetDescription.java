package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.description.FacetDescription;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFacetDescription implements FacetDescription {

  protected String facetName;
  protected FacetGetter facetGetter;
  protected PropertyValueGetter propertyValueGetter;
  protected String propertyName;

  public AbstractFacetDescription(String facetName, String propertyName, FacetGetter facetGetter,
                                  PropertyValueGetter propertyValueGetter) {
    this.facetName = facetName;
    this.facetGetter = facetGetter;
    this.propertyValueGetter = propertyValueGetter;
    this.propertyName = propertyName;
  }

  @Override
  public String getName() {
    return facetName;
  }

  @Override
  public Facet getFacet(Map<String, Set<Vertex>> values) {
    return facetGetter.getFacet(facetName, values);
  }

  @Override
  public List<String> getValues(Vertex vertex) {
    return propertyValueGetter.getValues(vertex, propertyName);
  }
}
