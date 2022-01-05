package nl.knaw.huygens.timbuctoo.server.healthchecks;

public class ElementValidationResult implements ValidationResult {
  private final boolean valid;
  private final String message;

  public ElementValidationResult(boolean valid, String message) {
    this.valid = valid;
    this.message = message;
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
