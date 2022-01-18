package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;

import java.io.IOException;
import java.util.List;

public class FacetValueDeserializer extends JsonDeserializer<FacetValue> {

  public static final String LOWER_LIMIT = "lowerLimit";
  public static final String UPPER_LIMIT = "upperLimit";

  @Override
  public FacetValue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
    throws IOException, JsonProcessingException {
    JsonNode facetValue = jsonParser.readValueAsTree();

    String name = facetValue.get("name").asText();

    if (facetValue.has("values")) {
      List<String> values = Lists.newArrayList();
      facetValue.get("values").elements().forEachRemaining(value -> values.add(value.asText()));
      return new ListFacetValue(name, values);
    } else if (facetValue.has(LOWER_LIMIT) && facetValue.has(UPPER_LIMIT)) {
      return new DateRangeFacetValue(name, facetValue.get(LOWER_LIMIT).asLong(), facetValue.get(UPPER_LIMIT).asLong());
    }
    throw new RuntimeException(String.format("Facet value with name '%s' is not a valid type", name));
  }
}
