package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.AltName;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class AltNamesConverterTest {

  public static final AltName ALT_NAME_1 = new AltName("type", "name");
  public static final AltName ALT_NAME_2 = new AltName("type1", "name1");
  private ObjectMapper objectMapper;
  private AltNamesConverter instance;

  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    instance = new AltNamesConverter();
  }

  @Test
  public void jsonToTinkerpopConvertsASerializedAltNamesValuesToADbSerializedAsString() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode jsonNode = objectMapper.valueToTree(Lists.newArrayList(ALT_NAME_1, ALT_NAME_2));

    String value = instance.jsonToTinkerpop(jsonNode);

    AltNames altNamesValue = objectMapper.readValue(value, AltNames.class);
    assertThat(altNamesValue.list, containsInAnyOrder(ALT_NAME_1, ALT_NAME_2));
  }

  @Test(expected = IOException.class)
  public void jsonToTinkerpopThrowsAnIoExceptionIfTheValueCannotBeConvertedToAltNames() throws Exception {
    JsonNode bogusInput = new ObjectMapper().createObjectNode();

    instance.jsonToTinkerpop(bogusInput);
  }

  @Test
  public void tinkerpopToJsonReadsTheListOfTheAltNamesValues() throws Exception {
    AltNames altNames = new AltNames();
    altNames.list = Lists.newArrayList(ALT_NAME_1, ALT_NAME_2);
    String dbValue = objectMapper.writeValueAsString(altNames);

    JsonNode jsonNode = instance.tinkerpopToJson(dbValue);

    List<AltName> altNameList = objectMapper.readValue(jsonNode.toString(), new TypeReference<List<AltName>>() {
    });

    assertThat(altNameList, containsInAnyOrder(ALT_NAME_1, ALT_NAME_2));
  }

  @Test(expected = IOException.class)
  public void tinkerpopToJsonThrowsAnIoExceptionWhenTheValueCannotBeParsed() throws Exception {
    String dbValue = "";

    instance.tinkerpopToJson(dbValue);
  }

}
