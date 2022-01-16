package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dataaccess.VreAuthorizationAccess;
import nl.knaw.huygens.timbuctoo.security.dto.UserRoles;
import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorizationStubs;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationUnavailableException;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.security.dto.UserRoles.ADMIN_ROLE;
import static nl.knaw.huygens.timbuctoo.security.dto.UserStubs.userWithPid;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsonBasedAuthorizerTest {

  public static final String VRE_ID = "vreId";
  public static final String USER_PID = "userPid";
  private VreAuthorizationAccess authorizationAccess;
  private JsonBasedAuthorizer instance;

  @Before
  public void setUp() throws Exception {
    authorizationAccess = mock(VreAuthorizationAccess.class);
    instance = new JsonBasedAuthorizer(authorizationAccess);
  }

  @Test
  public void createAuthorizationLetsCreatesANewAuthorizationForTheUserVreAndRole()
    throws Exception {
    instance.createAuthorization(VRE_ID, userWithPid(USER_PID), UserRoles.USER_ROLE);

    verify(authorizationAccess).getOrCreateAuthorization(VRE_ID, USER_PID, UserRoles.USER_ROLE);
  }

  @Test(expected = AuthorizationCreationException.class)
  public void createAuthorizationThrowsAnAuthCreateExWhenTheAuthorizationCollectionThrowsAnAuthUnavailableEx()
    throws Exception {
    when(authorizationAccess.getOrCreateAuthorization(anyString(), anyString(), anyString()))
      .thenThrow(new AuthorizationUnavailableException());

    instance.createAuthorization(VRE_ID, userWithPid(USER_PID), UserRoles.USER_ROLE);
  }

  @Test
  public void deleteVreAuthorizationsRemovesAllTheAuthorizationsOfTheVre() throws Exception {
    when(authorizationAccess.getAuthorization(VRE_ID, USER_PID)).thenReturn(Optional.of(
      VreAuthorizationStubs.authorizationWithRole(ADMIN_ROLE)));

    instance.deleteVreAuthorizations(VRE_ID);

    verify(authorizationAccess).deleteVreAuthorizations(VRE_ID);
  }
  
}
