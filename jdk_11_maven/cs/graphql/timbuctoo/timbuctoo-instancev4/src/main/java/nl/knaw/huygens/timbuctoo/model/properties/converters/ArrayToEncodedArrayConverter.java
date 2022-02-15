package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;

public class ArrayToEncodedArrayConverter implements Converter {

  static final String TYPE = "encoded-array";

  @Override
  public Object jsonToTinkerpop(JsonNode json) throws IOException {
    if (json.isArray()) {
      return json.toString();
    } else {
      throw new IOException("should be an Array.");
    }
  }

  ObjectMapper mapper = new ObjectMapper();

  @Override
  public ArrayNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String) {
      JsonNode jsonNode = mapper.readTree((String) value);
      if (jsonNode instanceof ArrayNode) {
        return (ArrayNode) jsonNode;
      } else {
        throw new IOException("is encoded JSON, but not an array: " + jsonNode.toString());
      }
    } else {
      throw new IOException("should be an string encoded Array");
    }
  }

  @SuppressWarnings("unchecked")
  public <E> E[] tinkerpopToJava(Object value, Class<? extends E[]> clazz) throws IOException {
    if (value instanceof String) {
      return mapper.readValue((String) value, clazz);
    } else {
      throw new IOException("should be an string encoded Array");
    }
  }

  @Override
  public String getGuiTypeId() {
    return "list-of-strings";
  }

  @Override
  public String getUniqueTypeIdentifier() {
    return TYPE;
  }
}
