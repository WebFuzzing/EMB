package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.LambdaExceptionUtil.rethrowConsumer;

public class HyperlinksConverter implements Converter {

  static final String TYPE = "hyperlinks";

  private void throwIfInvalid(JsonNode json) throws IOException {
    if (json instanceof ArrayNode) {
      json.forEach(rethrowConsumer(val -> {
        if (!val.isObject()) {
          throw new IOException("each item in the array should be an object node");
        } else {
          if (!val.has("url") || !val.get("url").isTextual()) {
            throw new IOException("each item in the array must have an url property containing a string");
          }
          if (!val.has("label") || !val.get("url").isTextual()) {
            throw new IOException("each item in the array must have an url property containing a string");
          }
        }
      }));
    } else {
      throw new IOException("should be an array.");
    }
  }

  @Override
  public Object jsonToTinkerpop(JsonNode json) throws IOException {
    throwIfInvalid(json);
    return json.toString();
  }

  @Override
  public ArrayNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String) {
      JsonNode result = new ObjectMapper().readTree((String) value);
      throwIfInvalid(result);
      return (ArrayNode) result;
    } else {
      throw new IOException("should be a string");
    }
  }

  public String getGuiTypeId() {
    return "links";
  }

  @Override
  public String getUniqueTypeIdentifier() {
    return TYPE;
  }
}
