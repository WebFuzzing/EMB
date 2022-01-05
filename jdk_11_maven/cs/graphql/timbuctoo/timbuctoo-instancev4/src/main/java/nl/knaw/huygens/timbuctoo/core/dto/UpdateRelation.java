package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.model.Change;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

public class UpdateRelation {
  private final UUID id;
  private Change modified;
  private int rev;
  private boolean accepted;

  public UpdateRelation(UUID id, int rev, boolean accepted) {
    this.id = id;
    this.rev = rev;
    this.accepted = accepted;
  }

  public UUID getId() {
    return id;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public void setModified(Change modified) {
    this.modified = modified;
  }

  public Change getModified() {
    return modified;
  }

  public int getRev() {
    return rev;
  }

  public boolean getAccepted() {
    return accepted;
  }
}
