package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.commons.lang3.StringUtils;

class DatableFromYearPropertyParser implements PropertyParser {

  @Override
  public String parse(String value) {
    if (value == null) {
      return null;
    }

    Datable datable = deserializeDatable(value);

    return datable.isValid() ? String.valueOf(datable.getFromYear()) : null;
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    if (value == null) {
      return null;
    }

    Datable datable = deserializeDatable(value);

    return datable.isValid() ?  datable.getFromYear() : null;
  }

  private Datable deserializeDatable(String value) {
    String cleanedValue = StringUtils.strip(value, "\"");
    return new Datable(cleanedValue);
  }
}
