package nl.knaw.huygens.timbuctoo.security.dataaccess;

import nl.knaw.huygens.timbuctoo.security.dto.Login;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LoginCreationException;

import java.util.Optional;

public interface LoginAccess {

  Optional<Login> getLogin(String username) throws LocalLoginUnavailableException;

  void addLogin(Login login) throws LoginCreationException;
}
