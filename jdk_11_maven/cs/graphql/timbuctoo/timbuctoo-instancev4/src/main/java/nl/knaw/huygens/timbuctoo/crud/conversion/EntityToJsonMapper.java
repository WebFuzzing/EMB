package nl.knaw.huygens.timbuctoo.crud.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;
import nl.knaw.huygens.timbuctoo.core.dto.RelationRef;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.crud.UrlGenerator;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.util.JsonBuilder;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;

public class EntityToJsonMapper {
  private static final Logger LOG = LoggerFactory.getLogger(EntityToJsonMapper.class);

  private final UserValidator userValidator;
  private final UrlGenerator relationUrlFor;

  public EntityToJsonMapper(UserValidator userValidator, UrlGenerator relationUrlFor) {
    this.userValidator = userValidator;
    this.relationUrlFor = relationUrlFor;
  }


  public interface ExtraEntityMappingOptions {
    void execute(ReadEntity readEntity, ObjectNode resultJson);
  }

  public interface ExtraRelationMappingOptions {
    void execute(RelationRef relationRef, ObjectNode resultJson);
  }

  public ObjectNode mapEntity(Collection collection, ReadEntity entity, boolean withRelations,
                              ExtraEntityMappingOptions extraEntityMappingOptions,
                              ExtraRelationMappingOptions relationMappingOptions) {
    final ObjectNode mappedEntity = JsonNodeFactory.instance.objectNode();
    String id = entity.getId().toString();

    mappedEntity.set("@type", jsn(collection.getEntityTypeName()));
    mappedEntity.set("_id", jsn(id));

    mappedEntity.set("^rev", jsn(entity.getRev()));
    mappedEntity.set("^deleted", jsn(entity.getDeleted()));
    mappedEntity.set("^pid", jsn(entity.getPid()));
    if (entity.getRdfUri() != null) {
      mappedEntity.set("^rdfUri", jsn(entity.getRdfUri().toString()));
    }
    mappedEntity.set("^rdfAlternatives", jsnA(entity.getRdfAlternatives().stream().map(JsonBuilder::jsn)));

    JsonNode variationRefs = jsnA(entity.getTypes()
                                        .stream()
                                        .map(type -> {
                                          ObjectNode variationRef = jsnO();
                                          variationRef.set("id", jsn(id));
                                          variationRef.set("type", jsn(type));
                                          return variationRef;
                                        }));
    mappedEntity.set("@variationRefs", variationRefs);

    Change modified = entity.getModified();
    mappedEntity.set("^modified", mapChange(modified));
    Change created = entity.getCreated();
    mappedEntity.set("^created", mapChange(created));

    // translate TimProperties to Json
    JsonPropertyConverter jsonPropertyConverter = new JsonPropertyConverter(collection);
    entity.getProperties().forEach(prop -> {
      try {
        Tuple<String, JsonNode> convertedProperty = prop.convert(jsonPropertyConverter);
        mappedEntity.set(convertedProperty.getLeft(), convertedProperty.getRight());
      } catch (IOException e) {
        LOG.error(databaseInvariant, propConversionErrorMessage(id, prop));
        LOG.error("Exception message: {}", e.getMessage());
        LOG.debug("Stack trace", e);
      }
    });

    if (!Strings.isNullOrEmpty(entity.getDisplayName())) {
      mappedEntity.set("@displayName", jsn(entity.getDisplayName()));
    }

    extraEntityMappingOptions.execute(entity, mappedEntity);

    if (withRelations) {
      mappedEntity.set("@relationCount", jsn(entity.getRelations().size()));
      mappedEntity.set("@relations", mapRelations(entity.getRelations(), relationMappingOptions));
    }
    return mappedEntity;
  }

  private String propConversionErrorMessage(String entityId, TimProperty<?> prop) {
    return String.format(
      "Property '%s' with value '%s' for entity with id '%s' cannot not be converted as '%s'",
      prop.getName(),
      prop.getValue(),
      entityId,
      prop.getPropertyType()
    );
  }

  private JsonNode mapRelations(List<RelationRef> relations,
                                ExtraRelationMappingOptions relationMappingOptions) {
    ObjectNode relationsNode = jsnO();
    relations.stream().map(relationRef -> mapRelationRef(relationRef, relationMappingOptions)
    ).collect(groupingBy(x -> x.get("relationType").asText())).entrySet().forEach(relationsType ->
      relationsNode.set(relationsType.getKey(), jsnA(relationsType.getValue().stream())));

    return relationsNode;
  }

  private ObjectNode mapRelationRef(RelationRef relationRef,
                                    ExtraRelationMappingOptions relationMappingOptions) {
    ObjectNode result = jsnO(
      tuple("id", jsn(relationRef.getEntityId())),
      tuple("path", jsn(relationUrlFor.apply(relationRef.getCollectionName(),
        UUID.fromString(relationRef.getEntityId()), null).toString())),
      tuple("relationType", jsn(relationRef.getRelationType())),
      tuple("type", jsn(relationRef.getEntityType())),
      tuple("accepted", jsn(relationRef.isRelationAccepted())),
      tuple("relationId", jsn(relationRef.getRelationId())),
      tuple("rev", jsn(relationRef.getRelationRev())),
      tuple("displayName", jsn(relationRef.getDisplayName()))
    );

    relationMappingOptions.execute(relationRef, result);

    return result;
  }

  private JsonNode mapChange(Change change) {
    String userId = change.getUserId();
    ObjectNode changeNode = new ObjectMapper().valueToTree(change);

    try {
      userValidator.getUserFromUserId(userId).ifPresent(user -> changeNode.set("username", jsn(user.getDisplayName())));
    } catch (UserValidationException e) {
      LOG.error("Could not retrieve user store", e);
    }

    return changeNode;
  }

}
