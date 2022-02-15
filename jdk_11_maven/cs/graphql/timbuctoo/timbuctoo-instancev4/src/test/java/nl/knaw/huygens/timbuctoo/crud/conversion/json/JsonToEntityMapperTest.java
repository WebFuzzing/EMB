package nl.knaw.huygens.timbuctoo.crud.conversion.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.crud.conversion.JsonToEntityMapper;
import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;
import nl.knaw.huygens.timbuctoo.util.JsonBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.localProperty;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class JsonToEntityMapperTest {

  @Test
  public void newCreateEntityMapsTheJsonObjectToACreateEntity() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "name", jsn("Hans"),
      "age", jsn("12")
    );
    JsonToEntityMapper instance = new JsonToEntityMapper();

    List<TimProperty<?>> properties = instance.getDataProperties(collection, input);

    assertThat(properties, containsInAnyOrder(
      allOf(hasProperty("name", equalTo("name")), hasProperty("value", equalTo("Hans"))),
      allOf(hasProperty("name", equalTo("age")), hasProperty("value", equalTo("12")))
    ));
  }

  @Test
  public void getDataPropertiesIgnoresPropertiesWithValueEmptyString() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "name", jsn("Hans"),
      "age", jsn("")
    );
    JsonToEntityMapper instance = new JsonToEntityMapper();

    List<TimProperty<?>> properties = instance.getDataProperties(collection, input);

    assertThat(properties, not(hasItem(hasProperty("name", equalTo("age")))));
  }

  @Test(expected = IOException.class)
  public void newCreateEntityThrowsAnIoExceptionWhenThePropertyIsUnknown() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "unknownProperty", jsn("value"),
      "age", jsn("12")
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    instance.getDataProperties(collection, input);
  }

  @Test(expected = IOException.class)
  public void newCreateEntityThrowsAnIoExceptionWhenThePropertyCannotBeConverted() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "name", jsn("Hans"),
      "age", jsn(12)
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    instance.getDataProperties(collection, input);
  }

  @Test
  public void newCreateEntityIgnoresThePrefixedFields() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons")
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "_id", jsn("id"),
      "^rev", jsn(1),
      "@type", jsn("wwperson")
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    List<TimProperty<?>> properties = instance.getDataProperties(collection, input);

    assertThat(properties, not(containsInAnyOrder(
      hasProperty("name", equalTo("_id")),
      hasProperty("name", equalTo("^rev")),
      hasProperty("name", equalTo("@type"))
    )));
  }

  @Test
  public void newUpdateEntityMapsTheJsonObjectToAnUpdateEntity() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    int rev = 2;
    ObjectNode input = JsonBuilder.jsnO(
      "name", jsn("Hans"),
      "age", jsn("12"),
      "^rev", jsn(rev)
    );
    JsonToEntityMapper instance = new JsonToEntityMapper();
    UUID id = UUID.randomUUID();

    UpdateEntity updateEntity = instance.newUpdateEntity(collection, id, input);

    assertThat(updateEntity.getProperties(), containsInAnyOrder(
      allOf(hasProperty("name", equalTo("name")), hasProperty("value", equalTo("Hans"))),
      allOf(hasProperty("name", equalTo("age")), hasProperty("value", equalTo("12")))
    ));
    assertThat(updateEntity.getId(), is(id));
    assertThat(updateEntity.getRev(), is(rev));
  }

  @Test(expected = IOException.class)
  public void newUpdateEntityThrowsAnIoExceptionWhenThePropertyIsUnknown() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "unknownProperty", jsn("value"),
      "age", jsn("12"),
      "^rev", jsn(2)
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    instance.newUpdateEntity(collection, UUID.randomUUID(), input);
  }

  @Test(expected = IOException.class)
  public void newUpdateEntityThrowsAnIoExceptionWhenThePropertyCannotBeConverted() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "name", jsn("Hans"),
      "age", jsn(12),
      "^rev", jsn(2)
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    instance.newUpdateEntity(collection, UUID.randomUUID(), input);
  }

  @Test
  public void newUpdateEntityThrowsAnIoExceptionWhenItDoesNotContainARevProperty() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons", c -> c
          .withProperty("name", localProperty("wwname"))
          .withProperty("age", localProperty("wwage"))
        )
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "name", jsn("Hans"),
      "age", jsn("12"),
      "^rev", jsn(2)
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    instance.newUpdateEntity(collection, UUID.randomUUID(), input);
  }

  @Test
  public void newUpdateEntityIgnoresThePrefixedFields() throws Exception {
    Collection collection = new VresBuilder()
      .withVre("WomenWriters", "ww", vre -> vre
        .withCollection("wwpersons")
      ).build().getCollection("wwpersons").get();
    ObjectNode input = JsonBuilder.jsnO(
      "_id", jsn("id"),
      "^rev", jsn(1),
      "@type", jsn("wwperson")
    );

    JsonToEntityMapper instance = new JsonToEntityMapper();

    UpdateEntity updateEntity = instance.newUpdateEntity(collection, UUID.randomUUID(), input);

    assertThat(updateEntity.getProperties(), not(containsInAnyOrder(
      hasProperty("name", equalTo("_id")),
      hasProperty("name", equalTo("^rev")),
      hasProperty("name", equalTo("@type"))
    )));
  }

}
