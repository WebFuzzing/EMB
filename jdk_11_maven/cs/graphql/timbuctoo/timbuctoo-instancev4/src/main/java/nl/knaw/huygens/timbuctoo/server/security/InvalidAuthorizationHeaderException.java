package nl.knaw.huygens.timbuctoo.server.security;

public class InvalidAuthorizationHeaderException extends Exception {
  InvalidAuthorizationHeaderException(String message) {
    super(message);
  }

}
