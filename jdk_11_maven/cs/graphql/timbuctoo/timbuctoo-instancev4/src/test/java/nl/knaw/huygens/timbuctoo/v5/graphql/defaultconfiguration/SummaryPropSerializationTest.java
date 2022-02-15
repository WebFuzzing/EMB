package nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class SummaryPropSerializationTest {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new GuavaModule());

  @Test
  public void simplePathDeserialization() throws IOException {
    String serializedValue = "{\n" +
      "    \"path\": [\n" +
      "      \"http://timbuctoo.huygens.knaw.nl/static/v5/predicate/names\"\n" +
      "    ],\n" +
      "    \"type\": \"SimplePath\"\n" +
      "  }";
    SummaryProp summaryProp = OBJECT_MAPPER.readValue(serializedValue, SummaryProp.class);

    assertThat(summaryProp, hasProperty("simplePath",
      contains("http://timbuctoo.huygens.knaw.nl/static/v5/predicate/names")
    ));
  }

  @Test
  public void simplePathSerialization() throws IOException {
    SimplePath input =
      SimplePath.create(Lists.newArrayList("http://timbuctoo.huygens.knaw.nl/static/v5/predicate/names"));

    String serializedValue = OBJECT_MAPPER.writeValueAsString(input);

    SummaryProp deserialized = OBJECT_MAPPER.readValue(serializedValue, SummaryProp.class);

    assertThat(deserialized, is(input));
  }

  @Test
  public void directionalPathDeserialization() throws IOException {
    String serializedValue = "{\n" +
      "      \"path\": [\n" +
      "        {\n" +
      "          \"step\": \"http://schema.org/Event/location_of_the_event/location_reference\",\n" +
      "          \"direction\": \"IN\"\n" +
      "        },\n" +
      "        {\n" +
      "          \"step\": \"http://schema.org/Event/location_of_the_event/location_reference_name\",\n" +
      "          \"direction\": \"OUT\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"type\": \"DirectionalPath\"\n" +
      "    }";
    SummaryProp summaryProp = OBJECT_MAPPER.readValue(serializedValue, SummaryProp.class);

    assertThat(summaryProp, hasProperty("path",
      contains(
        DirectionalStep.create("http://schema.org/Event/location_of_the_event/location_reference", Direction.IN),
        DirectionalStep.create("http://schema.org/Event/location_of_the_event/location_reference_name", Direction.OUT)
      )
    ));
  }

  @Test
  public void directionalPathSerialization() throws IOException {
    SummaryProp input = DirectionalPath.create(Lists.newArrayList(
      DirectionalStep.create("http://timbuctoo.huygens.knaw.nl/static/v5/predicate/names", Direction.OUT)));

    String serializedValue = OBJECT_MAPPER.writeValueAsString(input);

    SummaryProp deserialized = OBJECT_MAPPER.readValue(serializedValue, SummaryProp.class);

    assertThat(deserialized, is(input));
  }

}
