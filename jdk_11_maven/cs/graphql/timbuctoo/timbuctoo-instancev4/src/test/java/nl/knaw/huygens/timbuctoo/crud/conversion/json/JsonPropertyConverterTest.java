package nl.knaw.huygens.timbuctoo.crud.conversion.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.property.AltNamesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DatableProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.crud.conversion.JsonPropertyConverter;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.conversion.TinkerPopPropertyConverter;
import nl.knaw.huygens.timbuctoo.model.AltName;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.junit.Test;

import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class JsonPropertyConverterTest {
  @Test
  public void toConvertsTheListOfAltNamesToAnArrayNode() throws Exception {
    JsonPropertyConverter jsonPropertyConverter = new JsonPropertyConverter(null);
    AltNames altNames = new AltNames();
    altNames.list = Lists.newArrayList(new AltName("type1", "name1"), new AltName("type2", "name2"));

    Tuple<String, JsonNode> valueTuple = jsonPropertyConverter.to(new AltNamesProperty("test", altNames));

    JsonNode value = valueTuple.getRight();

    assertThat(value.isArray(), is(true));
    assertThat(Lists.newArrayList(value.iterator()), containsInAnyOrder(
      jsnO("nametype", jsn("type1"), "displayName", jsn("name1")),
      jsnO("nametype", jsn("type2"), "displayName", jsn("name2"))
    ));
  }

  @Test
  public void toConvertsADatablePropertyToAString() throws Exception {
    JsonPropertyConverter instance = new JsonPropertyConverter(null);
    DatableProperty datableProperty = new DatableProperty("", "2016-01-02");

    Tuple<String, JsonNode> value = instance.to(datableProperty);

    assertThat(value.getRight(), is(jsn("2016-01-02")));
  }

  // Datable tests
  @Test
  public void fromReturnsADatablePropertyWithADecodedStringValue() throws Exception {
    Collection collection = mock(Collection.class);
    ReadableProperty readableProperty = mock(ReadableProperty.class);
    when(readableProperty.getUniqueTypeId()).thenReturn("datable");
    when(collection.getProperty("prop")).thenReturn(Optional.of(readableProperty));
    JsonPropertyConverter instance = new JsonPropertyConverter(collection);

    TimProperty<?> from = instance.from("prop", jsn("1800"));

    assertThat(from.getValue(), is("1800"));
  }

  @Test
  public void toReturnsAJsonEncodedStringForADatableProperty() throws Exception {
    JsonPropertyConverter instance = new JsonPropertyConverter(null);
    DatableProperty property = new DatableProperty("prop", "1800");

    Tuple<String, JsonNode> value = instance.to(property);

    assertThat(value.getRight(), is(jsn("1800")));
  }
}
