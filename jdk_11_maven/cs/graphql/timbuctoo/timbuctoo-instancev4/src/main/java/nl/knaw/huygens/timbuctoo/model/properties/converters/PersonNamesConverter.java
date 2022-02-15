package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.model.properties.HasParts;

import java.io.IOException;
import java.util.Collection;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

public class PersonNamesConverter implements Converter, HasParts {

  static final String TYPE = "person-names";

  @Override
  public Object jsonToTinkerpop(JsonNode json) throws IOException {
    //convert to personNames as verification
    //make the same as the database value
    ObjectNode dbJson = jsnO("list", json);
    new ObjectMapper().treeToValue(dbJson, PersonNames.class);
    //if this doesn't throw then it was a good personName apparently
    return dbJson.toString();
  }

  public PersonNames tinkerpopToJava(Object value) throws IOException {
    if (value instanceof String) {
      return new ObjectMapper().readValue((String) value, PersonNames.class);
    } else {
      throw new IOException("must be a json value serialised as String");
    }
  }

  @Override
  public JsonNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String) {
      JsonNode json = new ObjectMapper().readTree((String) value);
      //convert to personNames as verification
      new ObjectMapper().treeToValue(json, PersonNames.class);
      //if this doesn't throw then it was a good personName apparently
      return json.get("list");
    } else {
      throw new IOException("must be a json value serialised as String");
    }
  }

  public String getGuiTypeId() {
    return "names";
  }

  @Override
  public String getUniqueTypeIdentifier() {
    return TYPE;
  }

  @Override
  public Collection<String> getParts() {
    return Lists.newArrayList("FORENAME", "SURNAME", "NAME_LINK", "ROLE_NAME", "GEN_NAME");
  }

}
