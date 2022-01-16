package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class StringToEncodedStringOfLimitedValuesConverter implements Converter, HasOptions {

  static final String TYPE = "encoded-string-of-limited-values";
  private final List<String> allowedValues;
  private final ObjectMapper objectMapper;

  public StringToEncodedStringOfLimitedValuesConverter(String... values) {
    this.allowedValues = Lists.newArrayList(values);
    objectMapper = new ObjectMapper();
  }

  public static void throwIfInvalid(JsonNode json, List<String> allowedValues) throws IOException {
    if (json.isTextual()) {
      String val = json.asText("");
      if (!allowedValues.contains(val)) {
        throw new IOException(val + " is not one of " + String.join(", ", allowedValues));
      }
    } else {
      throw new IOException("should be a string.");
    }
  }

  @Override
  public String jsonToTinkerpop(JsonNode json) throws IOException {
    throwIfInvalid(json, this.allowedValues);
    return json.toString();
  }

  @Override
  public TextNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String) {
      JsonNode result = objectMapper.readTree((String) value);
      throwIfInvalid(result, this.allowedValues);
      return (TextNode) result;
    } else {
      throw new IOException("should be a string");
    }
  }

  public String getGuiTypeId() {
    return "select";
  }

  @Override
  public String getUniqueTypeIdentifier() {
    return TYPE;
  }

  @Override
  public Collection<String> getOptions() {
    return this.allowedValues;
  }

  @Override
  public void validate(Object value) throws IOException {
    if (!(value instanceof String)) {
      throw new IOException("should be a string.");
    }
    JsonNode result = objectMapper.readTree((String) value);
    throwIfInvalid(result, this.allowedValues);
  }
}
