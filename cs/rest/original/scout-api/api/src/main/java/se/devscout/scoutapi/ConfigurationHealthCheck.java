package se.devscout.scoutapi;

import com.codahale.metrics.health.HealthCheck;

public class ConfigurationHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
