package nl.knaw.huygens.timbuctoo.server.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import nl.knaw.huygens.timbuctoo.server.BackgroundRunner;

import java.util.Optional;

public class DatabaseHealthCheck extends HealthCheck {

  private final BackgroundRunner<ValidationResult> validator;

  public DatabaseHealthCheck(BackgroundRunner<ValidationResult> validator) {
    this.validator = validator;
  }

  @Override
  protected Result check() throws Exception {
    final Optional<ValidationResult> mostRecentResult = validator.getMostRecentResult();
    if (!mostRecentResult.isPresent() || mostRecentResult.get().isValid()) {
      return Result.healthy();
    } else {
      return Result.unhealthy("Database invalid");
    }
  }
}
