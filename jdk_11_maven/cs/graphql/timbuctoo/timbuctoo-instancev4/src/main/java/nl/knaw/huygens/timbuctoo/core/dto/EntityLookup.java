package nl.knaw.huygens.timbuctoo.core.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableEntityLookup.class)
@JsonDeserialize(as = ImmutableEntityLookup.class)
public interface EntityLookup {
  /**
   * The fields here all have Optional as saving persistent Id for an entity works differently for old Timbuctoo
   * and new Timbuctoo.
   * Rev, Collection and TimId are required for the old implementation while DataSetId, Uri and User for the new one.
   */

  Optional<Integer> getRev();

  Optional<String> getCollection();

  Optional<UUID> getTimId();

  Optional<String> getDataSetId();

  Optional<String> getUri();

  Optional<User> getUser();
}
