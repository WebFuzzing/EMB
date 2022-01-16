package nl.knaw.huygens.timbuctoo.server.security;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LoginCreationException;
import nl.knaw.huygens.timbuctoo.security.LoginCreator;
import nl.knaw.huygens.timbuctoo.security.exceptions.UserCreationException;
import nl.knaw.huygens.timbuctoo.security.UserCreator;
import nl.knaw.huygens.timbuctoo.security.VreAuthorizationCrud;

import java.util.Map;

import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.EMAIL_ADDRESS;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.GIVEN_NAME;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.ORGANIZATION;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.PASSWORD;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.SURNAME;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.USER_NAME;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.USER_PID;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.VRE_ID;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.VRE_ROLE;

public class LocalUserCreator {

  private final LoginCreator loginCreator;
  private final UserCreator userCreator;
  private final VreAuthorizationCrud authorizationCreator;

  public LocalUserCreator(LoginCreator loginCreator, UserCreator userCreator,
                          VreAuthorizationCrud authorizationCreator) {

    this.loginCreator = loginCreator;
    this.userCreator = userCreator;
    this.authorizationCreator = authorizationCreator;
  }

  public void create(Map<String, String> userInfo) throws UserCreationException {
    String userPid = userInfo.get(USER_PID);
    String userName = userInfo.get(USER_NAME);
    String password = userInfo.get(PASSWORD);
    String givenName = userInfo.get(GIVEN_NAME);
    String surname = userInfo.get(SURNAME);
    String emailAddress = userInfo.get(EMAIL_ADDRESS);
    String organization = userInfo.get(ORGANIZATION);
    String vreId = userInfo.get(VRE_ID);
    String vreRole = userInfo.get(VRE_ROLE);

    try {
      loginCreator.createLogin(userPid, userName, password, givenName, surname, emailAddress, organization);
      User user = userCreator.createUser(userPid, emailAddress, givenName, surname, organization);
      authorizationCreator.createAuthorization(vreId, user, vreRole);
    } catch (LoginCreationException e) {
      throw new UserCreationException(e);
    } catch (AuthorizationCreationException e) {
      throw new UserCreationException(e);
    }
  }

}
