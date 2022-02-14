package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import nl.knaw.huygens.timbuctoo.search.FacetValue;

import java.util.List;

public class ListFacetValue implements FacetValue {

  private String name;
  private List<String> values;

  public ListFacetValue() {

  }

  public ListFacetValue(String name, List<String> values) {
    this.name = name;
    this.values = values;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
