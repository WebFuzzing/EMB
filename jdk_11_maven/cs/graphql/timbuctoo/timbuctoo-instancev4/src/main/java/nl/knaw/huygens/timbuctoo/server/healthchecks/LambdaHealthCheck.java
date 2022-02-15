package nl.knaw.huygens.timbuctoo.server.healthchecks;

import com.codahale.metrics.health.HealthCheck;

import java.util.Optional;
import java.util.function.Supplier;

public class LambdaHealthCheck extends HealthCheck {

  private final Supplier<Optional<String>> checkFunction;

  public LambdaHealthCheck(Supplier<Optional<String>> checkFunction) {
    this.checkFunction = checkFunction;
  }

  @Override
  protected Result check() throws Exception {
    return checkFunction.get()
      .map(Result::unhealthy)
      .orElse(Result.healthy());
  }
}
