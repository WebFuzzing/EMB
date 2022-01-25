package nl.knaw.huygens.timbuctoo.security.dataaccess;

import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationUnavailableException;

import java.util.Optional;

public interface VreAuthorizationAccess {
  VreAuthorization getOrCreateAuthorization(String vreId, String userId, String userRole)
    throws AuthorizationUnavailableException;

  Optional<VreAuthorization> getAuthorization(String vreId, String userId) throws AuthorizationUnavailableException;

  void deleteVreAuthorizations(String vreId) throws AuthorizationUnavailableException;
}
