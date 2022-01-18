package nl.knaw.huygens.timbuctoo.server.healthchecks;

import java.util.List;

public class CompositeValidationResult implements ValidationResult {
  private List<ValidationResult> validationResults;

  public CompositeValidationResult(List<ValidationResult> validationResults) {
    this.validationResults = validationResults;
  }

  @Override
  public boolean isValid() {
    return validationResults.stream().allMatch(result -> result.isValid());
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    validationResults.stream().filter(validationResult -> !validationResult.isValid())
                     .map(validationResult -> validationResult.getMessage())
                     .forEach(message -> sb.append(String.format("%s\n", message)));
    return sb.toString();
  }
}
