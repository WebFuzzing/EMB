package nl.knaw.huygens.timbuctoo.bulkupload;

public class InvalidFileException extends Exception {
  public InvalidFileException(String message, Exception inner) {
    super(message, inner);
  }

  public InvalidFileException(String message) {
    super(message);
  }

}
