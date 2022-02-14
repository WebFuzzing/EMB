package nl.knaw.huygens.timbuctoo.v5.dataset.exceptions;

public abstract class RdfProcessingParseException extends Exception {
  protected RdfProcessingParseException(String message) {
    super(message);
  }
}
