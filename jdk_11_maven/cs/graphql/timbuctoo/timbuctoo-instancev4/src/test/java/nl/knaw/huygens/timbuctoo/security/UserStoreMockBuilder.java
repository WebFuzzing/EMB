package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserStoreMockBuilder {

  private final UserStore userStore;

  private UserStoreMockBuilder() throws AuthenticationUnavailableException {
    userStore = mock(JsonBasedUserStore.class);
    when(userStore.userFor(anyString())).thenReturn(Optional.empty());
  }

  public UserStore build() {
    return userStore;
  }

  public static UserStoreMockBuilder userStore() throws AuthenticationUnavailableException {
    return new UserStoreMockBuilder();
  }

  public UserStoreMockBuilder withUserFor(String pid) throws AuthenticationUnavailableException {
    when(userStore.userFor(pid)).thenReturn(Optional.of(User.create("", "")));
    return this;
  }

}
