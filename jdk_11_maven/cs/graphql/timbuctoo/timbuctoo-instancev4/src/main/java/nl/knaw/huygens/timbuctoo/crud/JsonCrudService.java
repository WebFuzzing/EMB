package nl.knaw.huygens.timbuctoo.crud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.timbuctoo.core.AlreadyUpdatedException;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.TimbuctooActions;
import nl.knaw.huygens.timbuctoo.crud.conversion.EntityToJsonMapper;
import nl.knaw.huygens.timbuctoo.crud.conversion.JsonToEntityMapper;
import nl.knaw.huygens.timbuctoo.core.dto.CreateRelation;
import nl.knaw.huygens.timbuctoo.core.dto.DataStream;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateRelation;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

public class JsonCrudService {

  private final Vres mappings;
  private final TimbuctooActions timDbAccess;
  private final EntityToJsonMapper entityToJsonMapper;
  private final JsonToEntityMapper jsonToEntityMapper;

  public JsonCrudService(Vres mappings, UserValidator userValidator, UrlGenerator relationUrlFor,
                         TimbuctooActions timDbAccess) {
    this.mappings = mappings;
    this.timDbAccess = timDbAccess;
    entityToJsonMapper = new EntityToJsonMapper(userValidator, relationUrlFor);
    jsonToEntityMapper = new JsonToEntityMapper();
  }

  public UUID create(String collectionName, ObjectNode input, User user)
    throws InvalidCollectionException, IOException, PermissionFetchingException {

    final Collection collection = mappings.getCollection(collectionName)
                                          .orElseThrow(() -> new InvalidCollectionException(collectionName));

    try {
      if (collection.isRelationCollection()) {
        return createRelation(collection, input, user);
      } else {
        return createEntity(collection, input, user);
      }
    } catch (PermissionFetchingException e) {
      throw new IOException(e.getMessage());
    }
  }

  private UUID asUuid(ObjectNode input, String fieldName) throws IOException {
    try {
      return UUID.fromString(input.get(fieldName).asText(""));
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IOException(fieldName + " must contain a valid UUID", e);
    }
  }

  private UUID createRelation(Collection collection, ObjectNode input, User user)
    throws IOException, PermissionFetchingException {

    UUID sourceId = asUuid(input, "^sourceId");
    UUID targetId = asUuid(input, "^targetId");
    UUID typeId = asUuid(input, "^typeId");


    CreateRelation createRelation = new CreateRelation(sourceId, typeId, targetId);

    return timDbAccess.createRelation(collection, createRelation, user);
  }

  private UUID createEntity(Collection collection, ObjectNode input, User user)
    throws IOException, PermissionFetchingException {

    List<TimProperty<?>> timProperties = jsonToEntityMapper.getDataProperties(collection, input);

    Optional<Collection> baseCollection = mappings.getCollectionForType(collection.getAbstractType());

    UUID id = timDbAccess.createEntity(collection, baseCollection, timProperties, user);

    return id;
  }

  public JsonNode get(String collectionName, UUID id) throws InvalidCollectionException, NotFoundException {
    return get(collectionName, id, null);
  }

  public JsonNode get(String collectionName, UUID id, Integer rev)
    throws InvalidCollectionException, NotFoundException {

    final Collection collection = mappings.getCollection(collectionName)
                                          .orElseThrow(() -> new InvalidCollectionException(collectionName));

    if (collection.isRelationCollection()) {
      return jsnO("message", jsn("Getting a relation is not yet supported"));
    } else {
      return getEntity(id, rev, collection);
    }
  }

  private JsonNode getEntity(UUID id, Integer rev, Collection collection) throws NotFoundException {

    ReadEntity entity = timDbAccess.getEntity(collection, id, rev,
      (entity1, entityVertex) -> {

      },
      (traversalSource, vre, target, relationRef) -> {

      });

    ObjectNode result = entityToJsonMapper.mapEntity(collection, entity, true,
      (readEntity, resultJson) -> {
      },
      (relationRef, resultJson) -> {
      }
    );

    return result;
  }

  public List<ObjectNode> getCollection(String collectionName, int rows, int start, boolean withRelations)
    throws InvalidCollectionException {
    final Collection collection = mappings.getCollection(collectionName)
                                          .orElseThrow(() -> new InvalidCollectionException(collectionName));

    DataStream<ReadEntity> entities = timDbAccess.getCollection(collection, start, rows, withRelations,
      (traversalSource, vre) -> {

      },
      (entity1, entityVertex, target, relationRef) -> {

      }
    );
    List<ObjectNode> result = entities.map(entity ->
      entityToJsonMapper.mapEntity(collection, entity, withRelations,
        (readEntity, resultJson) -> {
        },
        (relationRef, resultJson) -> {
        })
    );
    return result;
  }


  public void replace(String collectionName, UUID id, ObjectNode data, User user)
    throws InvalidCollectionException, IOException, NotFoundException, AlreadyUpdatedException,
    PermissionFetchingException {

    final Collection collection = mappings.getCollection(collectionName)
                                          .orElseThrow(() -> new InvalidCollectionException(collectionName));


    if (collection.isRelationCollection()) {
      replaceRelation(collection, id, data, user);
    } else {
      replaceEntity(collection, id, data, user);
    }
  }

  private void replaceRelation(Collection collection, UUID id, ObjectNode data, User user)
    throws IOException, NotFoundException, PermissionFetchingException {

    JsonNode accepted = data.get("accepted");
    if (accepted == null || !accepted.isBoolean()) {
      throw new IOException("Only the property accepted can be updated. It must be a boolean");
    }
    JsonNode rev = data.get("^rev");
    if (rev == null || !rev.isNumber()) {
      throw new IOException("^rev must be a number");
    }

    for (Iterator<String> fieldNames = data.fieldNames(); fieldNames.hasNext(); ) {
      String name = fieldNames.next();
      if (!name.startsWith("^") && !name.startsWith("@") && !name.equals("_id") && !name.equals("accepted")) {
        throw new IOException("Only 'accepted' is a changeable property");
      }
    }

    UpdateRelation updateRelation = new UpdateRelation(id, rev.asInt(), accepted.asBoolean());

    timDbAccess.replaceRelation(collection, updateRelation, user);

  }

  private void replaceEntity(Collection collection, UUID id, ObjectNode data, User user)
    throws NotFoundException, IOException, AlreadyUpdatedException, PermissionFetchingException {

    UpdateEntity updateEntity = jsonToEntityMapper.newUpdateEntity(collection, id, data);

    timDbAccess.replaceEntity(collection, updateEntity, user);
  }

  public void delete(String collectionName, UUID id, User user)
    throws InvalidCollectionException, NotFoundException, PermissionFetchingException, IOException {

    final Collection collection = mappings.getCollection(collectionName)
                                          .orElseThrow(() -> new InvalidCollectionException(collectionName));
    try {
      timDbAccess.deleteEntity(collection, id, user);
    } catch (PermissionFetchingException e) {
      throw new IOException(e.getMessage());
    }

  }

  /**
   * Only added for the admin task AddTypeToNeo4JVertexTask
   * Do not use this method anywhere else
   */
  public void addTypeToEntity(UUID id, String typeToAdd)
    throws InvalidCollectionException, NotFoundException {
    final Collection newCollection = mappings.getCollection(typeToAdd)
                                          .orElseThrow(() -> new InvalidCollectionException(typeToAdd));

    timDbAccess.addTypeToEntity(id, newCollection);

  }

  public void moveEdges(int fromVertex, int toVertex) throws NotFoundException {
    timDbAccess.moveEdges(fromVertex, toVertex);
  }
}
