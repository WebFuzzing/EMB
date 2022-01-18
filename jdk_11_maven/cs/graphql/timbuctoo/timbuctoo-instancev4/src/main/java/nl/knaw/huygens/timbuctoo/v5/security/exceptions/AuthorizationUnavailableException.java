package nl.knaw.huygens.timbuctoo.v5.security.exceptions;

import java.io.IOException;

public class AuthorizationUnavailableException extends Exception {
  public AuthorizationUnavailableException(String message) {
    super(message);
  }

  public AuthorizationUnavailableException() {
    super();
  }
}
