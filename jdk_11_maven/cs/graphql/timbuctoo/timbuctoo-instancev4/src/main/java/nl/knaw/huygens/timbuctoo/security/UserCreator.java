package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.exceptions.UserCreationException;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

public interface UserCreator {
  /**
   * @return the internal user id.
   */
  User createUser(String pid, String email, String givenName, String surname, String organization)
    throws UserCreationException;
}
