package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import nl.knaw.huygens.timbuctoo.search.FacetValue;

public class DateRangeFacetValue implements FacetValue {
  private String name;
  private long lowerLimit;
  private long upperLimit;

  public DateRangeFacetValue(){

  }

  public DateRangeFacetValue(String name, long lowerLimit, long upperLimit) {
    this.name = name;
    this.lowerLimit = lowerLimit;
    this.upperLimit = upperLimit;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(long lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public long getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(long upperLimit) {
    this.upperLimit = upperLimit;
  }
}
