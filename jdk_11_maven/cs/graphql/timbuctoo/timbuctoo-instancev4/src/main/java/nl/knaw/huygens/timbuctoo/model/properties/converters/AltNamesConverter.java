package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.timbuctoo.model.AltNames;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static org.apache.commons.lang3.StringUtils.isBlank;

class AltNamesConverter implements Converter {

  static final String TYPE = "altnames";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String jsonToTinkerpop(JsonNode json) throws IOException {
    //convert to personNames as verification
    //make the same as the database value
    ObjectNode dbJson = jsnO("list", json);
    // verify json is an AltNames json
    objectMapper.treeToValue(dbJson, AltNames.class);
    //if this doesn't throw then it was a good personName apparently
    return dbJson.toString();
  }

  @Override
  public JsonNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String && !isBlank((String) value)) {
      JsonNode json = objectMapper.readTree((String) value);
      //convert to personNames as verification
      objectMapper.treeToValue(json, AltNames.class);
      //if this doesn't throw then it was a good personName apparently
      return json.get("list");
    } else {
      throw new IOException("must be a json value serialised as String");
    }
  }

  @Override
  public String getGuiTypeId() {
    return "altnames";
  }

  @Override
  public String getUniqueTypeIdentifier() {
    return TYPE;
  }
}
