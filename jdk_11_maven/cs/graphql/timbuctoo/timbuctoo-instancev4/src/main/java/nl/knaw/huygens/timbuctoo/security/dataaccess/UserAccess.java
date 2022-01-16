package nl.knaw.huygens.timbuctoo.security.dataaccess;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;

import java.util.Optional;

public interface UserAccess {
  void addUser(User user) throws AuthenticationUnavailableException;

  Optional<User> getUserForPid(String pid) throws AuthenticationUnavailableException;

  Optional<User> getUserForTimLocalId(String userId) throws AuthenticationUnavailableException;
}
