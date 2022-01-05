package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.commons.lang3.StringUtils;

public class TempNamePropertyParser implements PropertyParser {
  @Override
  public String parse(String value) {
    return "[TEMP] " + value;
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    return value == null ? null : StringUtils.stripToEmpty(value);
  }
}
