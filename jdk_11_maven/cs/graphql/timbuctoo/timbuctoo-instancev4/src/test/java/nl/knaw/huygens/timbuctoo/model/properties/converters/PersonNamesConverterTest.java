package nl.knaw.huygens.timbuctoo.model.properties.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class PersonNamesConverterTest {

  @Test
  public void canRoundTripExtendedName() throws IOException {
    ArrayNode inputJson = jsnA(
      jsnO(
        "components", jsnA(
          jsnO(
            "type", jsn("FORENAME"),
            "value", jsn("foreName1")
          ),
          jsnO(
            "type", jsn("SURNAME"),
            "value", jsn("surName1")
          )
        )
      ),
      jsnO(
        "components", jsnA(
          jsnO(
            "type", jsn("FORENAME"),
            "value", jsn("foreName2")
          ),
          jsnO(
            "type", jsn("SURNAME"),
            "value", jsn("surName2")
          )
        )
      )
    );

    PersonNamesConverter converter = new PersonNamesConverter();
    Object asTinkerpop = converter.jsonToTinkerpop(inputJson);
    JsonNode backToJson = converter.tinkerpopToJson(asTinkerpop);

    assertThat(backToJson.toString(), sameJSONAs(inputJson.toString()));
  }

  @Test
  public void usesServerStorageMethod() throws IOException {
    ArrayNode inputJson = jsnA(
      jsnO(
        "components", jsnA(
          jsnO(
            "type", jsn("FORENAME"),
            "value", jsn("foreName1")
          )
        )
      )
    );
    ObjectNode storageFormat = jsnO(
      "list", inputJson
    );

    PersonNamesConverter converter = new PersonNamesConverter();
    Object asTinkerpop = converter.jsonToTinkerpop(inputJson);

    assertThat(storageFormat.toString(), sameJSONAs((String) asTinkerpop));
  }

}
