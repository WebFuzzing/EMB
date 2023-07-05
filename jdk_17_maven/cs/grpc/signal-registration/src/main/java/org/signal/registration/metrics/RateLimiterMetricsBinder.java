/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micronaut.configuration.metrics.annotation.RequiresMetrics;
import io.micronaut.context.BeanProvider;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;
import org.signal.registration.ratelimit.RateLimitExceededException;
import org.signal.registration.ratelimit.RateLimiter;
import org.signal.registration.util.CompletionExceptions;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The rate limiter metrics binder wraps newly-created rate limiters in a proxy that measures the time taken to check
 * rate limits and the outcomes (whether the action was rate-limited) of rate limit checks.
 */
@Singleton
@RequiresMetrics
public class RateLimiterMetricsBinder implements BeanCreatedEventListener<RateLimiter<?>> {

  private final BeanProvider<MeterRegistry> meterRegistryProvider;

  private static final String CHECK_RATE_LIMIT_TIMER_NAME = MetricsUtil.name(RateLimiter.class, "checkRateLimit");
  private static final String NAME_TAG = "name";
  private static final String ACTION_RATE_LIMITED_TAG = "rateLimited";

  private static class InstrumentedRateLimiter<K> implements RateLimiter<K> {

    private final RateLimiter<K> delegate;
    private final MeterRegistry meterRegistry;
    private final String name;

    private InstrumentedRateLimiter(final RateLimiter<K> delegate,
        final MeterRegistry meterRegistry,
        final String name) {

      this.delegate = delegate;
      this.meterRegistry = meterRegistry;
      this.name = name;
    }

    @Override
    public CompletableFuture<Optional<Instant>> getTimeOfNextAction(final K key) {
      return delegate.getTimeOfNextAction(key);
    }

    @Override
    public CompletableFuture<Void> checkRateLimit(final K key) {
      final Timer.Sample sample = Timer.start();

      return delegate.checkRateLimit(key)
          .whenComplete((ignored, throwable) -> {
            final boolean actionRateLimited =
                CompletionExceptions.unwrap(throwable) instanceof RateLimitExceededException;

            sample.stop(meterRegistry.timer(CHECK_RATE_LIMIT_TIMER_NAME,
                NAME_TAG, name,
                ACTION_RATE_LIMITED_TAG, String.valueOf(actionRateLimited)));
          });
    }
  }

  public RateLimiterMetricsBinder(final BeanProvider<MeterRegistry> meterRegistryProvider) {
    this.meterRegistryProvider = meterRegistryProvider;
  }

  @Override
  public RateLimiter<?> onCreated(final BeanCreatedEvent<RateLimiter<?>> event) {
    return new InstrumentedRateLimiter<>(event.getBean(),
        meterRegistryProvider.get(),
        event.getBeanIdentifier().getName());
  }
}
