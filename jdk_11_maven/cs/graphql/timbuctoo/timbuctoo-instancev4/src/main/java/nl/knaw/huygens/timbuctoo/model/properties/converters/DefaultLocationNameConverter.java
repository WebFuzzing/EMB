package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import nl.knaw.huygens.timbuctoo.model.LocationNames;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;

public class DefaultLocationNameConverter implements Converter {

  static final String TYPE = "default-location-display-name";

  @Override
  public Object jsonToTinkerpop(JsonNode json) throws IOException {
    throw new IOException("can only be converted to json, not from json.");
  }

  @Override
  public TextNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String) {
      return jsn(new ObjectMapper().readValue((String) value, LocationNames.class).getDefaultName());
    } else {
      throw new IOException("should be a string");
    }
  }

  @Override
  public String getGuiTypeId() {
    return TYPE;
  }

  @Override
  public String getUniqueTypeIdentifier() {
    return TYPE;
  }
}
