package nl.knaw.huygens.timbuctoo.core.dto;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

/**
 * A RelationRef represents the target side of a relationship (labeled directed edge).
 * The source is not represented: the caller is responsible for remembering it.
 */
public class RelationRef {
  private final String entityId;
  private final String entityRdfUri;
  private final String[] entityAlternativeUris;
  private final String collectionName;
  private final String entityType;
  private final boolean relationAccepted;
  private final String relationId;
  private final String relationRdfUri;
  private final int relationRev;
  private final String relationType;
  private final String displayName;
  private final Map<String, Object> extraProperties;


  public RelationRef(String entityId, String entityRdfUri, String[] entityAlternativeUris, String collectionName,
                     String entityType, boolean relationAccepted, String relationId, String relationRdfUri,
                     int relationRev, String relationType, String displayName) {
    this.entityId = entityId;
    this.entityRdfUri = entityRdfUri;
    this.entityAlternativeUris = entityAlternativeUris;
    this.collectionName = collectionName;
    this.entityType = entityType;
    this.relationAccepted = relationAccepted;
    this.relationId = relationId;
    this.relationRdfUri = relationRdfUri;
    this.relationRev = relationRev;
    this.relationType = relationType;
    this.displayName = displayName;
    this.extraProperties = Maps.newHashMap();
  }

  /**
   * @return Id of target entity.
   */
  public String getEntityId() {
    return entityId;
  }

  /**
   * @return RDF URI of target entity.
   */
  public String getEntityRdfUri() {
    return entityRdfUri;
  }

  /**
   * @return Name of the collection to which this relationship belongs.
   */
  public String getCollectionName() {
    return collectionName;
  }

  /**
   * @return Type of target entity.
   */
  public String getEntityType() {
    return entityType;
  }

  public boolean isRelationAccepted() {
    return relationAccepted;
  }

  public String getRelationId() {
    return relationId;
  }

  public String getRelationRdfUri() {
    return relationRdfUri;
  }

  public int getRelationRev() {
    return relationRev;
  }

  public String getRelationType() {
    return relationType;
  }

  /**
   * @return Display name of the relation's target.
   */
  public String getDisplayName() {
    return displayName;
  }

  public void addExtraProperty(String key, Object value) {
    extraProperties.put(key, value);
  }

  public Optional<Object> getExtraProperty(String key) {
    return Optional.ofNullable(extraProperties.get(key));
  }

  public String[] getEntityAlternativeUris() {
    return entityAlternativeUris;
  }
}
