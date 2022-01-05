package nl.knaw.huygens.timbuctoo.core.dto;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface EntityRelation {

  boolean isAccepted();

  UUID getTimId();

  int getRevision();
}
