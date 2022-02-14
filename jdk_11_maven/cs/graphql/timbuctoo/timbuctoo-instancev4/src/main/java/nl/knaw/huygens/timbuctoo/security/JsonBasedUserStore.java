package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dataaccess.UserAccess;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.UserCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class JsonBasedUserStore implements UserStore, UserCreator {

  public static final Logger LOG = LoggerFactory.getLogger(JsonBasedUserStore.class);
  private final UserAccess userAccess;

  public JsonBasedUserStore(UserAccess userAccess) {
    this.userAccess = userAccess;
  }

  @Override
  public Optional<User> userFor(String pid) throws AuthenticationUnavailableException {
    return userAccess.getUserForPid(pid);
  }

  @Override
  public Optional<User> userForId(String userId) throws AuthenticationUnavailableException {
    return userAccess.getUserForTimLocalId(userId);
  }

  @Override
  public User saveNew(String displayName, String persistentId) throws AuthenticationUnavailableException {
    User nw = User.create(displayName, persistentId);
    userAccess.addUser(nw);
    return nw;
  }

  @Override
  public User createUser(String pid, String email, String givenName, String surname, String organization)
    throws UserCreationException {
    User user = User.create(String.format("%s %s", givenName, surname), pid);
    try {
      Optional<User> userForPid = userAccess.getUserForPid(pid);
      if (userForPid.isPresent()) {
        return userForPid.get();
      } else {
        userAccess.addUser(user);
        return user;
      }
    } catch (AuthenticationUnavailableException e) {
      throw new UserCreationException(e);
    }
  }
}
