package nl.knaw.huygens.timbuctoo.util;

import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.UserStore;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserStoreBuilder {

  private final UserStore userStore;

  private UserStoreBuilder() {
    userStore = mock(UserStore.class);
    try {
      when(userStore.userForId(anyString())).thenReturn(Optional.empty());
    } catch (AuthenticationUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  public static UserStoreBuilder newUserStore() {
    return new UserStoreBuilder();
  }

  public UserStoreBuilder withUser(String id, User user) {
    try {
      when(userStore.userForId(id)).thenReturn(Optional.of(user));
    } catch (AuthenticationUnavailableException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public UserStore build() {
    return userStore;
  }
}
