package nl.knaw.huygens.timbuctoo.server.security;

import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LoginCreationException;
import nl.knaw.huygens.timbuctoo.security.LoginCreator;
import nl.knaw.huygens.timbuctoo.security.exceptions.UserCreationException;
import nl.knaw.huygens.timbuctoo.security.UserCreator;
import nl.knaw.huygens.timbuctoo.security.VreAuthorizationCrud;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static nl.knaw.huygens.timbuctoo.security.dto.UserStubs.userWithId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LocalUserCreatorTest {

  private static final String PID = "pid";
  private static final String USER_NAME = "user_name";
  private static final String PWD = "pwd";
  private static final String GIVEN_NAME = "Given";
  private static final String SURNAME = "Sur";
  private static final String EMAIL = "test@example.com";
  private static final String ORGANIZATION = "Org";
  private static final User USER = userWithId("userId");
  private static final String VRE_ID = "VRE";
  private static final String VRE_ROLE = "role";
  private LoginCreator loginCreator;
  private UserCreator userCreator;
  private VreAuthorizationCrud authorizationCreator;
  private LocalUserCreator instance;
  private Map<String, String> userInfo;

  @Before
  public void setup() throws Exception {
    loginCreator = mock(LoginCreator.class);
    userCreator = mock(UserCreator.class);
    given(userCreator.createUser(anyString(), anyString(), anyString(), anyString(), anyString()))
      .willReturn(USER);
    authorizationCreator = mock(VreAuthorizationCrud.class);
    instance = new LocalUserCreator(loginCreator, userCreator, authorizationCreator);

    userInfo = Maps.newHashMap();
    userInfo.put("userPid", PID);
    userInfo.put("userName", USER_NAME);
    userInfo.put("password", PWD);
    userInfo.put("givenName", GIVEN_NAME);
    userInfo.put("surname", SURNAME);
    userInfo.put("emailAddress", EMAIL);
    userInfo.put("organization", ORGANIZATION);
    userInfo.put("vreId", VRE_ID);
    userInfo.put("vreRole", VRE_ROLE);
  }

  @Test
  public void createCreatesALoginAUserAndAVreAuthorization() throws Exception {
    instance.create(userInfo);

    verify(loginCreator).createLogin(PID, USER_NAME, PWD, GIVEN_NAME, SURNAME, EMAIL, ORGANIZATION);
    verify(userCreator).createUser(PID, EMAIL, GIVEN_NAME, SURNAME, ORGANIZATION);
    verify(authorizationCreator).createAuthorization(VRE_ID, USER, VRE_ROLE);
  }


  @Test(expected = UserCreationException.class)
  public void createThrowsAUserCreationExceptionWhenTheLoginCreatorThrowsALoginCreationException() throws Exception {
    Mockito.doThrow(new LoginCreationException("")).when(loginCreator)
           .createLogin(anyString(), anyString(), anyString(), anyString(), anyString(),
                                             anyString(),
                                             anyString());

    instance.create(userInfo);
  }

  @Test(expected = UserCreationException.class)
  public void createThrowsAUserCreationExceptionWhenTheUserCreatorDoes() throws Exception {
    doThrow(new UserCreationException("")).when(userCreator)
                                          .createUser(anyString(), anyString(), anyString(), anyString(), anyString());

    instance.create(userInfo);
  }

  @Test(expected = UserCreationException.class)
  public void createThrowsAUserCreationExceptionWhenTheAuthorizationCreatorThrowsAnAuthorizationCreationException()
    throws Exception {
    Mockito.doThrow(new AuthorizationCreationException("")).when(authorizationCreator)
           .createAuthorization(anyString(), any(User.class), anyString());

    instance.create(userInfo);
  }
}
