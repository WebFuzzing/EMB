package nl.knaw.huygens.concordion.extensions;

public class ValidationResult {
  public final boolean succeeded;
  private final String message;
  private final boolean isXml;

  public static ValidationResult result(boolean succeeded, String message) {
    return new ValidationResult(succeeded, message, false);
  }

  public static ValidationResult xmlResult(boolean succeeded, String message) {
    return new ValidationResult(succeeded, message, true);
  }

  public ValidationResult(boolean succeeded, String message, boolean isXml) {
    this.succeeded = succeeded;
    this.message = message;
    this.isXml = isXml;
  }

  public boolean isSucceeded() {
    return succeeded;
  }

  public String getMessage() {
    return message;
  }

  public boolean isXml() {
    return isXml;
  }
}
