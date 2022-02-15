package nl.knaw.huygens.timbuctoo.v5.security.openidconnect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import nl.knaw.huygens.timbuctoo.security.UserStore;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.util.Timeout;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Optional;

class OpenIdConnectUserValidator implements UserValidator {
  private final Cache<String, User> users;
  private final OpenIdClient openIdClient;
  private final UserStore userStore;

  OpenIdConnectUserValidator(Timeout inactivityTimeout, OpenIdClient openIdClient, UserStore userStore) {
    users = createCache(inactivityTimeout);
    this.openIdClient = openIdClient;
    this.userStore = userStore;
  }

  private static Cache<String, User> createCache(Timeout timeout) {
    return CacheBuilder.newBuilder().expireAfterAccess(timeout.duration, timeout.timeUnit).build();
  }

  @Override
  public Optional<User> getUserFromAccessToken(String accessToken) throws UserValidationException {
    if (StringUtils.isBlank(accessToken)) {
      return Optional.empty();
    }

    final User local = users.getIfPresent(accessToken);
    if (local != null) {
      return Optional.of(local);
    }

    try {
      final Optional<UserInfo> userInfoOpt = openIdClient.getUserInfo(accessToken);
      if (userInfoOpt.isEmpty()) {
        return Optional.empty();
      }

      final UserInfo userInfo = userInfoOpt.get();

      final String subject = userInfo.getSubject().getValue();
      final Optional<User> user = userStore.userFor(subject);
      if (user.isPresent()) {
        user.ifPresent(value -> users.put(accessToken, value));
        return user;
      } else {
        final User newUser = userStore.saveNew(userInfo.getNickname(), subject);
        users.put(subject, newUser);
        return Optional.of(newUser);
      }

    } catch (AuthenticationUnavailableException | IOException | ParseException e) {
      throw new UserValidationException(e);
    }

  }

  @Override
  public Optional<User> getUserFromUserId(String userId) throws UserValidationException {
    try {
      return userStore.userForId(userId);
    } catch (AuthenticationUnavailableException e) {
      throw new UserValidationException(e);
    }
  }

  @Override
  public Optional<User> getUserFromPersistentId(String persistentId) throws UserValidationException {
    try {
      return userStore.userFor(persistentId);
    } catch (AuthenticationUnavailableException e) {
      throw new UserValidationException(e);
    }
  }
}
