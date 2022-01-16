package nl.knaw.huygens.timbuctoo.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Immutable
@JsonSerialize(as = ImmutableVreAuthorization.class)
@JsonDeserialize(as = ImmutableVreAuthorization.class)
public abstract class VreAuthorization implements Authorization {

  public static VreAuthorization create(String vreId, String userId, String... roles) {
    return ImmutableVreAuthorization.builder()
      .vreId(vreId)
      .userId(userId)
      .addRoles(roles)
      .build();
  }

  public abstract String getVreId();

  public abstract String getUserId();

  public abstract List<String> getRoles();

  @JsonIgnore
  @Override
  public boolean isAllowedToWrite() {
    return UserRoles.getVerified().stream().anyMatch(getRoles()::contains);
  }

  @JsonIgnore
  @Override
  public boolean hasAdminAccess() {
    return getRoles().contains(UserRoles.ADMIN_ROLE);
  }

}
