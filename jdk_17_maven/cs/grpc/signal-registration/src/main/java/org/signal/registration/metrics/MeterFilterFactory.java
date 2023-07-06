/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.signal.registration.util.InstanceIdSupplier;

@Factory
public class MeterFilterFactory {

  private static final DistributionStatisticConfig DEFAULT_DISTRIBUTION_STATISTIC_CONFIG =
      DistributionStatisticConfig.builder()
          .percentiles(.5, .95, .99, .999)
          .build();

  @Bean
  @Singleton
  MeterFilter instanceIdTagFilter() {
    return MeterFilter.commonTags(Tags.of("instance", InstanceIdSupplier.getInstanceId()));
  }

  @Bean
  @Singleton
  MeterFilter distributionSummaryConfigurationFilter() {
    return new MeterFilter() {
      @Override
      public DistributionStatisticConfig configure(final Meter.Id id, final DistributionStatisticConfig config) {
        return config.merge(DEFAULT_DISTRIBUTION_STATISTIC_CONFIG);
      }
    };
  }
}
