package nl.knaw.huygens.timbuctoo.crud.conversion.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntityImpl;
import nl.knaw.huygens.timbuctoo.core.dto.RelationRef;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.property.HyperLinksProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.StringProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.crud.conversion.EntityToJsonMapper;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

// TODO before refactoring this class improve coverage
public class EntityToJsonMapperTest {

  public static final String USER_ID = "userId";
  public static final String USER_NAME = "User Name";
  private UserValidator userValidator;
  private EntityToJsonMapper instance;

  @Before
  public void setUp() throws Exception {
    userValidator = mock(UserValidator.class);
    when(userValidator.getUserFromUserId(USER_ID)).thenReturn(Optional.of(User.create(USER_NAME, "")));
    instance = new EntityToJsonMapper(
      userValidator,
      (collection, id1, rev) -> URI.create("www.example.com")
    );
  }

  @Test
  public void mapEntityMapsTheTypeAndId() throws Exception {
    ReadEntityImpl readEntity = new ReadEntityImpl();
    UUID id = UUID.randomUUID();
    readEntity.setId(id);
    Change change = new Change(Instant.now().toEpochMilli(), USER_ID, null);
    readEntity.setCreated(change);
    readEntity.setModified(change);
    String type = "otherType";
    readEntity.setTypes(Lists.newArrayList("type", type));
    readEntity.setDeleted(false);
    readEntity.setRev(1);
    readEntity.setPid("pid");
    readEntity.setProperties(Lists.newArrayList());

    EntityToJsonMapper instance = new EntityToJsonMapper(
      userValidator,
      (collection, id1, rev) -> URI.create("www.example.com")
    );
    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn(type);

    ObjectNode resutlJson = instance.mapEntity(
      collection,
      readEntity,
      false,
      (readEntity1, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );


    assertThat(
      resutlJson.toString(),
      sameJSONAs(jsnO("_id", jsn(id.toString()), "@type", jsn(type)).toString()).allowingExtraUnexpectedFields()
    );
  }

  @Test
  public void mapEntityMapsTheProperties() throws Exception {
    ReadEntityImpl readEntity = new ReadEntityImpl();
    readEntity.setId(UUID.randomUUID());
    String userId = "userId";
    Change change = new Change(Instant.now().toEpochMilli(), userId, null);
    readEntity.setCreated(change);
    readEntity.setModified(change);
    String type = "otherType";
    readEntity.setTypes(Lists.newArrayList("type", type));
    readEntity.setDeleted(false);
    readEntity.setRev(1);
    readEntity.setPid("pid");
    ArrayList<TimProperty<?>> properties = Lists.newArrayList();
    properties.add(new StringProperty("name", "Name"));
    readEntity.setProperties(properties);
    EntityToJsonMapper instance = new EntityToJsonMapper(
      userValidator,
      (collection, id1, rev) -> URI.create("www.example.com")
    );
    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn(type);

    ObjectNode resutlJson = instance.mapEntity(
      collection,
      readEntity,
      false,
      (readEntity1, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );


    assertThat(
      resutlJson.toString(),
      sameJSONAs(jsnO("name", jsn("Name")).toString()).allowingExtraUnexpectedFields()
    );
  }

  @Test
  public void doesNotAddNonConvertableProperties() {
    ReadEntityImpl readEntity = new ReadEntityImpl();
    readEntity.setId(UUID.randomUUID());
    String userId = "userId";
    Change change = new Change(Instant.now().toEpochMilli(), userId, null);
    readEntity.setCreated(change);
    readEntity.setModified(change);
    String type = "otherType";
    readEntity.setTypes(Lists.newArrayList("type", type));
    readEntity.setDeleted(false);
    readEntity.setRev(1);
    readEntity.setPid("pid");
    ArrayList<TimProperty<?>> properties = Lists.newArrayList();
    properties.add(new HyperLinksProperty("nonParsableProp", "Name"));
    readEntity.setProperties(properties);
    EntityToJsonMapper instance = new EntityToJsonMapper(
      userValidator,
      (collection, id1, rev) -> URI.create("www.example.com")
    );
    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn(type);

    ObjectNode resutlJson = instance.mapEntity(
      collection,
      readEntity,
      false,
      (readEntity1, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );

    assertThat(Lists.newArrayList(resutlJson.fieldNames()), not(hasItem("nonParsableProp")));
  }

  @Test
  public void mapEntityMapsTheRelations() throws Exception {
    ReadEntityImpl readEntity = new ReadEntityImpl();
    UUID id = UUID.randomUUID();
    readEntity.setId(id);
    String userId = "userId";
    Change change = new Change(Instant.now().toEpochMilli(), userId, null);
    readEntity.setCreated(change);
    readEntity.setModified(change);
    String type = "otherType";
    readEntity.setTypes(Lists.newArrayList("type", type));
    readEntity.setDeleted(false);
    readEntity.setRev(1);
    readEntity.setPid("pid");
    readEntity.setProperties(Lists.newArrayList());
    String otherEntity = UUID.randomUUID().toString();
    String relType = "relType";
    readEntity.setRelations(
      Lists.newArrayList(new RelationRef(otherEntity, "rdfUri", new String[]{"origUri"}, "otherColl", "otherType",
        true, "relId", "rdfUri", 1, relType, "displayName")));
    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn(type);

    ObjectNode resutlJson = instance.mapEntity(
      collection,
      readEntity,
      true,
      (readEntity1, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );

    assertThat(resutlJson.toString(), sameJSONAs(jsnO(
      "@relationCount", jsn(1),
      "@relations", jsnO(
        relType, jsnA(
          jsnO(
            "id", jsn(otherEntity)
          )
        )
      )
    ).toString()).allowingExtraUnexpectedFields());
  }

  @Test
  public void mapEntityDoesNotAddADisplayPropertyWhenTheDisplayNameIsNull() {
    ReadEntityImpl readEntity = new ReadEntityImpl();
    UUID id = UUID.randomUUID();
    readEntity.setId(id);
    String userId = "userId";
    Change change = new Change(Instant.now().toEpochMilli(), userId, null);
    readEntity.setCreated(change);
    readEntity.setModified(change);
    String type = "otherType";
    readEntity.setTypes(Lists.newArrayList("type", type));
    readEntity.setDeleted(false);
    readEntity.setRev(1);
    readEntity.setPid("pid");
    readEntity.setProperties(Lists.newArrayList());
    readEntity.setRelations(Lists.newArrayList());
    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn(type);

    ObjectNode resutlJson = instance.mapEntity(
      collection,
      readEntity,
      true,
      (readEntity1, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );

    assertThat(resutlJson.has("@displayName"), is(false));
  }

  @Test
  public void mapEntityAddsADisplayNameWhenItIsKnown() {
    ReadEntityImpl readEntity = new ReadEntityImpl();
    UUID id = UUID.randomUUID();
    readEntity.setId(id);
    String userId = "userId";
    Change change = new Change(Instant.now().toEpochMilli(), userId, null);
    readEntity.setCreated(change);
    readEntity.setModified(change);
    String type = "otherType";
    readEntity.setTypes(Lists.newArrayList("type", type));
    readEntity.setDeleted(false);
    readEntity.setRev(1);
    readEntity.setPid("pid");
    readEntity.setProperties(Lists.newArrayList());
    readEntity.setRelations(Lists.newArrayList());
    String displayName = "displayName";
    readEntity.setDisplayName(displayName);
    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn(type);

    ObjectNode resutlJson = instance.mapEntity(
      collection,
      readEntity,
      true,
      (readEntity1, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );

    assertThat(resutlJson.has("@displayName"), is(true));
    assertThat(resutlJson.get("@displayName"), is(jsn(displayName)));
  }

}
