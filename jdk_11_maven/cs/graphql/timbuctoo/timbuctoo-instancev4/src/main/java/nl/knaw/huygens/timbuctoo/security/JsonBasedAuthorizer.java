package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dataaccess.VreAuthorizationAccess;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationUnavailableException;

import java.util.Optional;

public class JsonBasedAuthorizer implements VreAuthorizationCrud {

  private VreAuthorizationAccess authorizationAccess;

  public JsonBasedAuthorizer(VreAuthorizationAccess authorizationAccess) {
    this.authorizationAccess = authorizationAccess;
  }

  @Override
  public Optional<VreAuthorization> getAuthorization(String vreId, User user)
    throws AuthorizationUnavailableException {
    return authorizationAccess.getAuthorization(vreId, user.getPersistentId());
  }


  @Override
  public void createAuthorization(String vreId, User user, String vreRole) throws AuthorizationCreationException {
    try {
      authorizationAccess.getOrCreateAuthorization(vreId, user.getPersistentId(), vreRole);
    } catch (AuthorizationUnavailableException e) {
      throw new AuthorizationCreationException(e);
    }
  }


  @Override
  public void deleteVreAuthorizations(String vreId)
    throws AuthorizationUnavailableException {

    authorizationAccess.deleteVreAuthorizations(vreId);
  }
}
