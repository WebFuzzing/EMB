package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.model.Change;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

public class CreateRelation {
  private UUID id;
  private Change created;
  private final UUID sourceId;
  private final UUID typeId;
  private final UUID targetId;

  public CreateRelation(UUID sourceId, UUID typeId, UUID targetId) {
    this.sourceId = sourceId;
    this.typeId = typeId;
    this.targetId = targetId;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Change getCreated() {
    return created;
  }

  public void setCreated(Change created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public UUID getSourceId() {
    return sourceId;
  }

  public UUID getTypeId() {
    return typeId;
  }

  public UUID getTargetId() {
    return targetId;
  }
}
