package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;

import java.util.Optional;

public interface Authenticator {
  Optional<String> authenticate(String username, String password) throws LocalLoginUnavailableException;
}
