package nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions;

public class DatabaseWriteException extends Exception {
  public DatabaseWriteException(Exception cause) {
    super(cause);
  }
}
