package nl.knaw.huygens.timbuctoo.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nl.knaw.huygens.security.client.AuthenticationHandler;
import nl.knaw.huygens.security.client.UnauthorizedException;
import nl.knaw.huygens.security.client.model.SecurityInformation;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Will determine if a user's credentials are valid and return a token.
 * Will be able to return the user belonging to a token.
 * <p/>
 * <p>Needs: An object from which to retrieve valid users, an object to store logged in users</p>
 */
public class LoggedInUsers {

  public static final Logger LOG = LoggerFactory.getLogger(LoggedInUsers.class);
  private final Authenticator authenticator;
  private final UserStore userStore;
  private final AuthenticationHandler authenticationHandler;
  private final Cache<String, User> users;

  public LoggedInUsers(Authenticator authenticator, UserStore userStore,
                       Timeout inactivityTimeout, AuthenticationHandler authenticationHandler) {
    this.authenticator = authenticator;
    this.userStore = userStore;
    this.authenticationHandler = authenticationHandler;
    this.users = createCache(inactivityTimeout);
  }

  private static Cache<String, User> createCache(Timeout timeout) {
    return CacheBuilder.newBuilder().expireAfterAccess(timeout.duration, timeout.timeUnit).build();
  }

  public synchronized Optional<User> userFor(String authHeader) {
    if (authHeader == null || authHeader.isEmpty()) {
      return Optional.empty();
    } else {
      User local = users.getIfPresent(authHeader);
      if (local != null) {
        return Optional.of(local);
      } else {
        try {
          SecurityInformation securityInformation = authenticationHandler.getSecurityInformation(authHeader);

          //get the one that was saved to the file
          Optional<User> userFromFile = userStore.userFor(securityInformation.getPersistentID());
          if (userFromFile.isPresent()) {
            users.put(authHeader, userFromFile.get());
            return userFromFile;
          } else {
            User nw = userStore.saveNew(securityInformation.getDisplayName(), securityInformation.getPersistentID());

            users.put(authHeader, nw);
            return Optional.of(nw);
          }
        } catch (UnauthorizedException e) {
          LOG.warn("User is not retrievable", e);
          return Optional.empty();
        } catch (IOException | AuthenticationUnavailableException e) {
          LOG.error("An exception is thrown while retrieving the user information.", e);
          return Optional.empty();
        }
      }
    }
  }

  public synchronized Optional<String> userTokenFor(String username, String password)
    throws LocalLoginUnavailableException, AuthenticationUnavailableException {
    Optional<String> id;
    Optional<String> token = Optional.empty();

    id = authenticator.authenticate(username, password);

    if (id.isPresent()) {
      Optional<User> user = userStore.userFor(id.get());
      if (user.isPresent()) {
        String uuid = UUID.randomUUID().toString();
        token = Optional.of(uuid);
        users.put(uuid, user.get());
      }
    }
    return token;
  }
}
