/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.ratelimit;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.signal.registration.rpc.MessageTransport;
import org.signal.registration.session.RegistrationSession;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
@Named("send-voice-verification-code")
public class SendVoiceVerificationCodeRateLimiter extends FixedDelayRegistrationSessionRateLimiter {

  private final Duration delayAfterFirstSms;

  public SendVoiceVerificationCodeRateLimiter(
      @Value("${rate-limits.send-voice-verification-code.delay-after-first-sms}") final Duration delayAfterFirstSms,
      @Value("${rate-limits.send-voice-verification-code.delays}") final List<Duration> delays,
      final Clock clock) {

    super(delays, clock);

    this.delayAfterFirstSms = delayAfterFirstSms;
  }

  @Override
  public CompletableFuture<Optional<Instant>> getTimeOfNextAction(final RegistrationSession session) {
    final Optional<Instant> maybeFirstAllowableVoiceCall =
        session.getRegistrationAttemptsList().stream()
            .filter(attempt -> attempt.getMessageTransport() == MessageTransport.MESSAGE_TRANSPORT_SMS)
            .findFirst()
            .map(firstSmsAttempt -> Instant.ofEpochMilli(firstSmsAttempt.getTimestampEpochMillis()).plus(delayAfterFirstSms));

    return CompletableFuture.completedFuture(maybeFirstAllowableVoiceCall.flatMap(firstAllowableVoiceCall -> {
      final Instant currentTime = getClock().instant();

      if (firstAllowableVoiceCall.isAfter(currentTime)) {
        // We're still waiting on the post-first-SMS delay
        return Optional.of(firstAllowableVoiceCall);
      }

      // We've cleared the post-first-SMS delay and should do the normal thing
      return super.getTimeOfNextAction(session).join();
    }));
  }

  @Override
  protected int getPriorAttemptCount(final RegistrationSession session) {
    return (int) session.getRegistrationAttemptsList().stream()
        .filter(attempt -> attempt.getMessageTransport() == MessageTransport.MESSAGE_TRANSPORT_VOICE)
        .count();
  }

  @Override
  protected Optional<Instant> getLastAttemptTime(final RegistrationSession session) {
    return session.getRegistrationAttemptsList().stream()
        .filter(attempt -> attempt.getMessageTransport() == MessageTransport.MESSAGE_TRANSPORT_VOICE)
        .map(attempt -> Instant.ofEpochMilli(attempt.getTimestampEpochMillis()))
        .max(Comparator.naturalOrder());
  }
}
