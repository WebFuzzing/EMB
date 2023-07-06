/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.ratelimit;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.signal.registration.rpc.MessageTransport;
import org.signal.registration.session.RegistrationSession;

@Singleton
@Named("send-sms-verification-code")
public class SendSmsVerificationCodeRateLimiter extends FixedDelayRegistrationSessionRateLimiter {

  public SendSmsVerificationCodeRateLimiter(
      @Value("${rate-limits.send-sms-verification-code.delays}") final List<Duration> delays,
      final Clock clock) {

    super(delays, clock);
  }

  @Override
  protected int getPriorAttemptCount(final RegistrationSession session) {
    return (int) session.getRegistrationAttemptsList().stream()
        .filter(attempt -> attempt.getMessageTransport() == MessageTransport.MESSAGE_TRANSPORT_SMS)
        .count();
  }

  @Override
  protected Optional<Instant> getLastAttemptTime(final RegistrationSession session) {
    return session.getRegistrationAttemptsList().stream()
        .filter(attempt -> attempt.getMessageTransport() == MessageTransport.MESSAGE_TRANSPORT_SMS)
        .map(attempt -> Instant.ofEpochMilli(attempt.getTimestampEpochMillis()))
        .max(Comparator.naturalOrder());
  }
}
