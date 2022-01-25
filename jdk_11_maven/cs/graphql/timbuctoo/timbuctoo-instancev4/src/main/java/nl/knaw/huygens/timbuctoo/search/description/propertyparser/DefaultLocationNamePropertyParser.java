package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.LocationNames;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class DefaultLocationNamePropertyParser implements PropertyParser {

  public static final Logger LOG = LoggerFactory.getLogger(DefaultLocationNamePropertyParser.class);

  @Override
  public String parse(String value) {
    if (value == null) {
      return null;
    }
    try {
      return new ObjectMapper().readValue(value, LocationNames.class).getDefaultName();
    } catch (IOException e) {
      LOG.error("'{}' could not be parsed to LocationNames", value);
      LOG.error("Exception thrown", e);
    }
    return null;
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    String parsedValue = parse(value);
    return parsedValue == null ? null : StringUtils.strip(parsedValue);
  }

}
