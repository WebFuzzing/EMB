package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface Converter {
  Object jsonToTinkerpop(JsonNode json) throws IOException;

  JsonNode tinkerpopToJson(Object value) throws IOException;

  String getGuiTypeId();

  String getUniqueTypeIdentifier();

  default void validate(Object value) throws IOException{
    // no validation needed by default.
  }
}
