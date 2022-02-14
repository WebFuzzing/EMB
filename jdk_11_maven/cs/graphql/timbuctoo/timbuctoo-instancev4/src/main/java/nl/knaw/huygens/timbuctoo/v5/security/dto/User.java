package nl.knaw.huygens.timbuctoo.v5.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonTypeIdResolver(UserTypeIdResolver.class) // be able to map the login java type and the serialized version
@JsonIgnoreProperties(ignoreUnknown = true) // ignore the unknown properties
@Value.Immutable
@JsonSerialize(as = ImmutableUser.class)
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
  static User create(String displayname, String persistentId, String id) {
    return ImmutableUser.builder()
      .displayName(displayname)
      .persistentId(persistentId)
      .id(id)
      .build();
  }

  static User create(String displayname, String persistentId) {
    return create(displayname, persistentId, UUID.randomUUID().toString());
  }

  @Nullable
  String getDisplayName();

  @Nullable
  String getPersistentId();

  @JsonProperty("_id")
  String getId();

}
