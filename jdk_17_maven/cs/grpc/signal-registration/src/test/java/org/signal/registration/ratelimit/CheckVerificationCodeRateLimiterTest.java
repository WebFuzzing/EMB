/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.ratelimit;

import org.junit.jupiter.api.Test;
import org.signal.registration.session.RegistrationSession;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckVerificationCodeRateLimiterTest {

  @Test
  void getPriorAttemptCount() {
    final CheckVerificationCodeRateLimiter rateLimiter =
        new CheckVerificationCodeRateLimiter(List.of(Duration.ZERO), Clock.systemUTC());

    assertEquals(0, rateLimiter.getPriorAttemptCount(RegistrationSession.newBuilder().build()));
    assertEquals(12,
        rateLimiter.getPriorAttemptCount(RegistrationSession.newBuilder().setCheckCodeAttempts(12).build()));
  }

  @Test
  void getLastAttemptTime() {
    final CheckVerificationCodeRateLimiter rateLimiter =
        new CheckVerificationCodeRateLimiter(List.of(Duration.ZERO), Clock.systemUTC());

    assertEquals(Optional.empty(), rateLimiter.getLastAttemptTime(RegistrationSession.newBuilder().build()));

    final long currentTimeMillis = System.currentTimeMillis();

    assertEquals(Optional.of(Instant.ofEpochMilli(currentTimeMillis)),
        rateLimiter.getLastAttemptTime(RegistrationSession.newBuilder()
            .setCheckCodeAttempts(1)
            .setLastCheckCodeAttemptEpochMillis(currentTimeMillis)
            .build()));

    assertEquals(Optional.empty(),
        rateLimiter.getLastAttemptTime(RegistrationSession.newBuilder()
            .setCheckCodeAttempts(0)
            .setLastCheckCodeAttemptEpochMillis(currentTimeMillis)
            .build()));
  }
}
