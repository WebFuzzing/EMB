package nl.knaw.huygens.timbuctoo.v5.security.twitterexample;

import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TwitterUserValidator implements UserValidator {
  private Twitter twitter = TwitterFactory.getSingleton();
  private Map<String, User> users = new HashMap<>();

  @Override
  public Optional<User> getUserFromAccessToken(String accessToken) throws UserValidationException {
    String token = accessToken.substring(0, accessToken.indexOf("_"));
    String tokenSecret = accessToken.substring(accessToken.indexOf("_") + 1, accessToken.length());

    if (users.get(token) != null) {
      return Optional.of(users.get(token));
    } else {
      twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
      twitter4j.User userTwitter;

      try {
        userTwitter = twitter.showUser(Long.valueOf(twitter.getId()));
      } catch (TwitterException e) {
        return Optional.empty();
      }

      User user = User.create(userTwitter.getName(), userTwitter.getId() + "",
        userTwitter.getId() + "");

      users.put(token, user);
      return Optional.of(user);
    }
  }

  @Override
  public Optional<User> getUserFromUserId(String userId) throws UserValidationException {
    twitter4j.User user;
    try {
      user = twitter.showUser(Long.valueOf(userId));
    } catch (TwitterException e) {
      throw new UserValidationException(e);
    }
    return Optional.of(User.create(user.getScreenName(), userId, userId));
  }

  @Override
  public Optional<User> getUserFromPersistentId(String persistentId) throws UserValidationException {
    // for twitter persistentId and userId are the same
    return getUserFromUserId(persistentId);
  }
}
