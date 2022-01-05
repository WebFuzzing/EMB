package nl.knaw.huygens.timbuctoo.database.tinkerpop;

public class ObjectSuddenlyDisappearedException extends RuntimeException {
  public ObjectSuddenlyDisappearedException(String message) {
    super(message);
  }
}
