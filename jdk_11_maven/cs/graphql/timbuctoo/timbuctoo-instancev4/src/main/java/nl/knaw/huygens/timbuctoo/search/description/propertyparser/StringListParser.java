package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;

public class StringListParser implements PropertyParser {
  private String separator;
  private ObjectMapper objectMapper;
  private static final Logger LOG = LoggerFactory.getLogger(StringListParser.class);

  public StringListParser(String seperator) {
    this.separator = seperator;
    objectMapper = new ObjectMapper();
    
  }

  public StringListParser() {
    this(";");
  }

  @Override
  public String parse(String value) {
    if (value == null) {
      return null;
    }
    try {
      List<String> readValue = objectMapper.readValue(value, new TypeReference<List<String>>() {});
      return Joiner.on(separator).join(readValue);
    } catch (IOException e) {
      LOG.error("Coud not parse {} to List of Strings", value);
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Comparable<?> parseForSort(String value) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
