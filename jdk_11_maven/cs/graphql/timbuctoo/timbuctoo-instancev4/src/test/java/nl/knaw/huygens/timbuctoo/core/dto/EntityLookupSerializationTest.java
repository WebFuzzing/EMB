package nl.knaw.huygens.timbuctoo.core.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EntityLookupSerializationTest {
  @Test
  public void entitySerializesForOldCase() throws Exception {
    EntityLookup entityLookup = ImmutableEntityLookup.builder()
      .rev(1)
      .timId(UUID.randomUUID())
      .collection("collection")
      .build();

    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.registerModule(new Jdk8Module());

    String entityString = objectMapper.writeValueAsString(entityLookup);

    EntityLookup entityLookup2 = objectMapper.readValue(entityString, EntityLookup.class);

    assertThat(entityLookup2, is(entityLookup));
  }

  @Test
  public void entitySerializesForNewCase() throws Exception {
    EntityLookup entityLookup = ImmutableEntityLookup.builder()
      .dataSetId("testUser_testDataSet")
      .user(User.create("testdisplayname","testpersistendid"))
      .uri("testUri")
      .build();

    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.registerModule(new Jdk8Module());

    String entityString = objectMapper.writeValueAsString(entityLookup);

    EntityLookup entityLookup2 = objectMapper.readValue(entityString, EntityLookup.class);

    assertThat(entityLookup2, is(entityLookup));
  }
}
