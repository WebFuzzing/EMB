package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.commons.lang3.StringUtils;

class StringPropertyParser implements PropertyParser {
  @Override
  public String parse(String value) {
    return value;
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    String parsedValue = parse(value);
    return parsedValue == null ? null : StringUtils.stripToEmpty(parsedValue);
  }

}
