package nl.knaw.huygens.timbuctoo.security.exceptions;

public class UserCreationException extends Exception {

  public UserCreationException(Exception exception) {
    super(exception);
  }

  public UserCreationException(String message) {
    super(message);
  }
}
