package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface ContextData {

  UserPermissionCheck getUserPermissionCheck();

  Optional<User> getUser();

  static ContextData contextData(UserPermissionCheck userPermissionCheck, Optional<User> user) {
    return ImmutableContextData.builder().userPermissionCheck(userPermissionCheck).user(user).build();
  }
}
