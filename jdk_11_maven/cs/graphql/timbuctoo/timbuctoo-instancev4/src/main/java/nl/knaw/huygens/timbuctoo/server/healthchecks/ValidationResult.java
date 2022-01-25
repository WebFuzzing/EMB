package nl.knaw.huygens.timbuctoo.server.healthchecks;

public interface ValidationResult {
  boolean isValid();

  String getMessage();
}
