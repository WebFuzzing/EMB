package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.Change;

import java.util.List;
import java.util.UUID;

public class UpdateEntity {
  private final UUID id;
  private final List<TimProperty<?>> properties;
  private final int rev;
  private Change modified;

  public UpdateEntity(UUID id, List<TimProperty<?>> properties, int rev) {
    this.id = id;
    this.properties = properties;
    this.rev = rev;
  }

  public UUID getId() {
    return id;
  }

  public List<TimProperty<?>> getProperties() {
    return properties;
  }

  public int getRev() {
    return rev;
  }

  public void setModified(Change modified) {
    this.modified = modified;
  }

  public Change getModified() {
    return modified;
  }
}
