package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.immutables.value.Value;

import java.util.UUID;


@Value.Immutable
public interface CreateEntity {

  Iterable<TimProperty<?>> getProperties();

  UUID getId();

  Change getCreated();

}
