package nl.knaw.huygens.timbuctoo.v5.security.exceptions;

public class AuthorizationCreationException extends Exception {
  public AuthorizationCreationException(String message) {
    super(message);
  }

  public AuthorizationCreationException(AuthorizationUnavailableException cause) {
    super(cause);
  }
}
