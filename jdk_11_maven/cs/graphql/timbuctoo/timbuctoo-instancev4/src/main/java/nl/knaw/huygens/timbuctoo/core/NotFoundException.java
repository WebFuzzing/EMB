package nl.knaw.huygens.timbuctoo.core;

public class NotFoundException extends Exception {
  public NotFoundException() {
    super();
  }

  public NotFoundException(String message) {
    super(message);
  }
}
