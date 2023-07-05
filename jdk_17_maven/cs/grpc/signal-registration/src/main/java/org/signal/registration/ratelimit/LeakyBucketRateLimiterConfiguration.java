/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.ratelimit;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import java.time.Duration;

@EachProperty("rate-limits.leaky-bucket")
public record LeakyBucketRateLimiterConfiguration(@Parameter String name,
                                                  int maxCapacity,
                                                  Duration permitRegenerationPeriod,
                                                  Duration minDelay) {
}
