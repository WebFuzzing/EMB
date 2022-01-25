package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthorizationException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationUnavailableException;

import java.util.Optional;

public interface VreAuthorizationCrud {
  void createAuthorization(String vreId, User user, String vreRole) throws AuthorizationCreationException;

  /**
   * Removes all the authorizations from the Vre.
   *
   * @param vreId the id of the Vre of which all the authorizations should be removed from.
   * @throws AuthorizationUnavailableException when the authorizations could not be read
   */
  void deleteVreAuthorizations(String vreId) throws AuthorizationUnavailableException;

  Optional<VreAuthorization> getAuthorization(String vreId, User user) throws AuthorizationUnavailableException;

}
