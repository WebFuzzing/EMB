package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.Change;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;

public class CreateEntityStubs {

  public static CreateEntity dummy() {
    return ImmutableCreateEntity.builder()
      .properties(newArrayList())
      .id(UUID.randomUUID())
      .created(new Change(Instant.now().toEpochMilli(), "userId", null))
      .build();
  }

  public static CreateEntity withProperties(List<TimProperty<?>> properties) {
    return ImmutableCreateEntity.builder()
      .properties(properties)
      .id(UUID.randomUUID())
      .created(new Change(Instant.now().toEpochMilli(), "userId", null))
      .build();
  }

  public static CreateEntity withProperties(List<TimProperty<?>> properties, String userId, long timeStamp) {
    return ImmutableCreateEntity.builder()
      .properties(properties)
      .id(UUID.randomUUID())
      .created(new Change(timeStamp, userId, null))
      .build();
  }

}
