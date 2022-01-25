package nl.knaw.huygens.timbuctoo.crud.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.timbuctoo.core.dto.property.AltNamesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.ArrayOfLimitedValuesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.ArrayProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DatableProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DefaultFullPersonNameProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DefaultLocationNameProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.EncodedStringOfLimitedValuesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.HyperLinksProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.PersonNamesProperty;
import nl.knaw.huygens.timbuctoo.core.PropertyConverter;
import nl.knaw.huygens.timbuctoo.core.dto.property.StringOfLimitedValuesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.StringProperty;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import nl.knaw.huygens.timbuctoo.model.EdtfPattern;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.timbuctoo.util.LambdaExceptionUtil.rethrowConsumer;

public class JsonPropertyConverter extends PropertyConverter<JsonNode> {

  private final ObjectMapper objectMapper;

  public JsonPropertyConverter(Collection collection) {
    super(collection);
    objectMapper = new ObjectMapper();
  }

  @Override
  protected AltNamesProperty createAltNamesProperty(String propertyName, JsonNode value) throws IOException {
    return new AltNamesProperty(propertyName, objectMapper.treeToValue(value, AltNames.class));
  }

  @Override
  protected DatableProperty createDatableProperty(String propertyName, JsonNode value) throws IOException {
    if (!value.isTextual()) {
      throw new IOException(
        String.format("Could not convert '%s', datable should be presented as String", propertyName)
      );
    }

    return new DatableProperty(propertyName, value.asText());
  }

  @Override
  protected DefaultFullPersonNameProperty createDefaultFullPersonNameProperty(String propertyName, JsonNode value)
    throws IOException {
    throw readOnlyProperty(propertyName, DefaultFullPersonNameProperty.class);
  }

  @Override
  protected DefaultLocationNameProperty createDefaultLocationNameProperty(String propertyName, JsonNode value)
    throws IOException {
    throw readOnlyProperty(propertyName, DefaultFullPersonNameProperty.class);
  }

  @Override
  protected HyperLinksProperty createHyperLinksProperty(String propertyName, JsonNode value) throws IOException {
    if (value instanceof ArrayNode) {
      value.forEach(rethrowConsumer(val -> {
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
      return new HyperLinksProperty(propertyName, objectMapper.writeValueAsString(value));
    }
    throw new IOException(String.format("'%s' should be an array of hyperlinks.", propertyName));
  }

  @Override
  protected PersonNamesProperty createPersonNamesProperty(String propertyName, JsonNode value)
    throws IOException {
    ObjectNode dbJson = jsnO("list", value);
    return new PersonNamesProperty(propertyName, objectMapper.treeToValue(dbJson, PersonNames.class));
  }

  @Override
  protected ArrayOfLimitedValuesProperty createArrayOfLimitedValuesProperty(String propertyName, JsonNode value)
    throws IOException {
    return new ArrayOfLimitedValuesProperty(propertyName, objectMapper.writeValueAsString(value));
  }

  @Override
  protected ArrayProperty createArrayProperty(String propertyName, JsonNode value) throws IOException {
    return new ArrayProperty(propertyName, objectMapper.writeValueAsString(value));
  }

  @Override
  protected EncodedStringOfLimitedValuesProperty createEncodedStringOfLimitedValuesProperty(String propertyName,
                                                                                            JsonNode value)
    throws IOException {
    return new EncodedStringOfLimitedValuesProperty(propertyName, objectMapper.writeValueAsString(value));
  }

  @Override
  protected StringProperty createStringProperty(String propertyName, JsonNode value) throws IOException {
    if (!value.isTextual()) {
      throw new IOException(String.format("'%s' should be a String.", propertyName));
    }
    return new StringProperty(propertyName, value.asText(""));
  }

  @Override
  protected StringOfLimitedValuesProperty createStringOfLimitedValues(String propertyName, JsonNode value)
    throws IOException {
    if (!value.isTextual()) {
      throw new IOException(String.format("'%s' should be a String.", propertyName));
    }
    return new StringOfLimitedValuesProperty(propertyName, value.asText());
  }

  @Override
  public Tuple<String, JsonNode> to(AltNamesProperty property) throws IOException {
    try {
      return new Tuple<>(property.getName(), objectMapper.valueToTree(property.getValue().list));
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  @Override
  public Tuple<String, JsonNode> to(DatableProperty property) throws IOException {
    return new Tuple<>(property.getName(), jsn(property.getValue()));
  }

  @Override
  public Tuple<String, JsonNode> to(DefaultFullPersonNameProperty property) throws IOException {
    return new Tuple<>(property.getName(), jsn(property.getValue().defaultName().getFullName()));
  }

  @Override
  public Tuple<String, JsonNode> to(DefaultLocationNameProperty property) throws IOException {
    return new Tuple<>(property.getName(), jsn(property.getValue().getDefaultName()));
  }

  @Override
  public Tuple<String, JsonNode> to(HyperLinksProperty property) throws IOException {
    return new Tuple<>(property.getName(), objectMapper.readTree(property.getValue()));
  }

  @Override
  public Tuple<String, JsonNode> to(PersonNamesProperty property) throws IOException {
    return new Tuple<>(property.getName(), objectMapper.valueToTree(property.getValue().list));
  }

  @Override
  public Tuple<String, JsonNode> to(ArrayOfLimitedValuesProperty property) throws IOException {
    return new Tuple<>(property.getName(), objectMapper.readTree(property.getValue()));
  }

  @Override
  public Tuple<String, JsonNode> to(ArrayProperty property) throws IOException {

    return new Tuple<>(property.getName(), objectMapper.readTree(property.getValue()));
  }

  @Override
  public Tuple<String, JsonNode> to(EncodedStringOfLimitedValuesProperty property) throws IOException {
    return new Tuple<>(property.getName(), objectMapper.readTree(property.getValue()));
  }

  @Override
  public Tuple<String, JsonNode> to(StringProperty property) throws IOException {
    return new Tuple<>(property.getName(), jsn(property.getValue()));
  }

  @Override
  public Tuple<String, JsonNode> to(StringOfLimitedValuesProperty property) throws IOException {
    return new Tuple<>(property.getName(), jsn(property.getValue()));
  }

}
