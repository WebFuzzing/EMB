package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;

import java.util.Optional;

class BasicUserValidator implements UserValidator {

  private final UserStore userStore;
  private final LoggedInUsers loggedInUsers;

  BasicUserValidator(UserStore userStore, LoggedInUsers loggedInUsers) {
    this.userStore = userStore;
    this.loggedInUsers = loggedInUsers;
  }

  @Override
  public Optional<User> getUserFromAccessToken(String accessToken) throws UserValidationException {
    if (accessToken != null) {
      try {
        Optional<User> user = loggedInUsers.userFor(accessToken);
        return user;
      } catch (Exception e) {
        throw new UserValidationException(e);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> getUserFromUserId(String userId) throws UserValidationException {
    if (userId != null) {
      try {
        return userStore.userForId(userId);
      } catch (AuthenticationUnavailableException e) {
        throw new UserValidationException(e);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> getUserFromPersistentId(String persistentId) throws UserValidationException {
    if (persistentId == null) {
      return Optional.empty();
    }

    try {
      return  userStore.userFor(persistentId);
    } catch (AuthenticationUnavailableException e) {
      throw new UserValidationException(e);
    }
  }

}
