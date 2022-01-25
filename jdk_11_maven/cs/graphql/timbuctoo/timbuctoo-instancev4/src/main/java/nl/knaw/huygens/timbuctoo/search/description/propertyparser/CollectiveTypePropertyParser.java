package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.commons.lang3.StringUtils;

public class CollectiveTypePropertyParser implements PropertyParser {
  @Override
  public String parse(String value) {
    if (value == null) {
      return null;
    }

    return StringUtils.strip(value, "\"");
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    return parse(value);
  }

}

