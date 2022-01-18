package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class PersonNamesDefaultNamePropertyParser implements PropertyParser {

  private static final Logger LOG = LoggerFactory.getLogger(PersonNamesDefaultNamePropertyParser.class);

  @Override
  public String parse(String value) {
    if (value != null) {
      try {
        PersonNames personNames = readPersonNames(value);

        return personNames.defaultName().getFullName();
      } catch (IOException e) {
        LOG.error("Cannot parse '{}' as PersonNames", value);
        LOG.error("Exception thrown", e);
      }
    }

    return null;
  }

  protected PersonNames readPersonNames(String value) throws IOException {
    return new ObjectMapper().readValue(value, PersonNames.class);
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    if (value != null) {
      try {
        PersonNames personNames = readPersonNames(value);

        String defaultName = personNames.defaultName().getSortName();
        if (StringUtils.isBlank(defaultName)) {
          return null;
        }
        return defaultName;
      } catch (IOException e) {
        LOG.error("Cannot parse '{}' as PersonNames", value);
        LOG.error("Exception thrown", e);
      }
    }

    return null;
  }

}
