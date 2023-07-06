/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.twilio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.verify.v2.VerificationAttempt;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micronaut.context.event.ApplicationEventPublisher;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.analytics.AttemptAnalysis;
import org.signal.registration.analytics.AttemptAnalyzedEvent;
import org.signal.registration.analytics.AttemptPendingAnalysis;
import org.signal.registration.analytics.AttemptPendingAnalysisRepository;
import org.signal.registration.analytics.Money;
import org.signal.registration.sender.twilio.verify.TwilioVerifySender;
import reactor.core.publisher.Flux;

class TwilioVerifyAttemptAnalyzerTest {

  private AttemptPendingAnalysisRepository repository;
  private ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher;

  private TwilioVerifyAttemptAnalyzer twilioVerifyAttemptAnalyzer;

  private static final String EMPTY_VERIFICATION_ATTEMPT_JSON = """
      {
        "sid": "empty-verification-attempt"
      }
      """;

  private static final String MISSING_PRICE_VERIFICATION_ATTEMPT_JSON = """
      {
        "sid": "missing-price",
        "channel_data": {
          "mcc": "123",
          "mnc": "456"
        }
      }
      """;

  private static final String MISSING_MCC_MNC_VERIFICATION_ATTEMPT_JSON = """
      {
        "sid": "missing-mcc-mnc",
        "price": {
          "value": "0.005",
          "currency": "usd"
        }
      }
      """;

  private static final String COMPLETE_VERIFICATION_ATTEMPT_JSON = """
      {
        "sid": "complete-attempt",
        "price": {
          "value": "0.005",
          "currency": "usd"
        },
        "channel_data": {
          "mcc": "123",
          "mnc": "456"
        }
      }
      """;

  @BeforeEach
  void setUp() {
    repository = mock(AttemptPendingAnalysisRepository.class);

    //noinspection unchecked
    attemptAnalyzedEventPublisher = mock(ApplicationEventPublisher.class);

    twilioVerifyAttemptAnalyzer = new TwilioVerifyAttemptAnalyzer(mock(TwilioRestClient.class),
        repository,
        attemptAnalyzedEventPublisher,
        "verify-service-sid",
        new SimpleMeterRegistry());
  }

  @ParameterizedTest
  @MethodSource
  void analyzeAttempt(final VerificationAttempt verificationAttempt,
      final boolean hasAttemptPendingAnalysis,
      @Nullable final AttemptAnalysis expectedAnalysis) {

    final AttemptPendingAnalysis attemptPendingAnalysis = AttemptPendingAnalysis.newBuilder()
        .setRemoteId(verificationAttempt.getSid())
        .build();

    when(repository.getByRemoteIdentifier(TwilioVerifySender.SENDER_NAME, verificationAttempt.getSid()))
        .thenReturn(CompletableFuture.completedFuture(
            hasAttemptPendingAnalysis ? Optional.of(attemptPendingAnalysis) : Optional.empty()));

    when(repository.remove(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

    twilioVerifyAttemptAnalyzer.analyzeAttempts(Flux.just(verificationAttempt));

    if (expectedAnalysis != null) {
      verify(repository).remove(TwilioVerifySender.SENDER_NAME, verificationAttempt.getSid());
      verify(attemptAnalyzedEventPublisher).publishEvent(new AttemptAnalyzedEvent(attemptPendingAnalysis, expectedAnalysis));
    } else {
      verify(repository, never()).remove(any(), any());
      verify(attemptAnalyzedEventPublisher, never()).publishEvent(any());
    }
  }

  private static Stream<Arguments> analyzeAttempt() {
    final ObjectMapper objectMapper = new ObjectMapper();

    final AttemptAnalysis missingMccMncAnalysis = new AttemptAnalysis(
        Optional.of(new Money(new BigDecimal("0.005"), Currency.getInstance("USD"))),
        Optional.empty(),
        Optional.empty());

    final AttemptAnalysis completeAnalysis = new AttemptAnalysis(
        Optional.of(new Money(new BigDecimal("0.005"), Currency.getInstance("USD"))),
        Optional.of("123"),
        Optional.of("456"));

    return Stream.of(
        Arguments.of(VerificationAttempt.fromJson(EMPTY_VERIFICATION_ATTEMPT_JSON, objectMapper), false, null),
        Arguments.of(VerificationAttempt.fromJson(EMPTY_VERIFICATION_ATTEMPT_JSON, objectMapper), true, null),
        Arguments.of(VerificationAttempt.fromJson(MISSING_PRICE_VERIFICATION_ATTEMPT_JSON, objectMapper), true, null),
        Arguments.of(VerificationAttempt.fromJson(MISSING_MCC_MNC_VERIFICATION_ATTEMPT_JSON, objectMapper), true, missingMccMncAnalysis),
        Arguments.of(VerificationAttempt.fromJson(COMPLETE_VERIFICATION_ATTEMPT_JSON, objectMapper), true, completeAnalysis),
        Arguments.of(VerificationAttempt.fromJson(COMPLETE_VERIFICATION_ATTEMPT_JSON, objectMapper), false, null)
    );
  }
}
