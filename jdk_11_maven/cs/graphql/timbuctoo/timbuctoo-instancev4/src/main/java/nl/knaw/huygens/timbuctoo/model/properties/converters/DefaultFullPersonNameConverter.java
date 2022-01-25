package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;

public class DefaultFullPersonNameConverter implements Converter {

  static final String TYPE = "default-person-display-name";

  private final PersonNamesConverter converter;

  public DefaultFullPersonNameConverter() {
    converter = new PersonNamesConverter();
  }

  @Override
  public Object jsonToTinkerpop(JsonNode json) throws IOException {
    throw new IOException("can only be converted to json, not from json.");
  }

  @Override
  public TextNode tinkerpopToJson(Object value) throws IOException {
    return jsn(converter.tinkerpopToJava(value).defaultName().getFullName());
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
