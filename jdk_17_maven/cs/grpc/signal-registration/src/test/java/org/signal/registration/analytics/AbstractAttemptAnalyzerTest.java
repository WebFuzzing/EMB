/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.scheduling.TaskScheduler;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class AbstractAttemptAnalyzerTest {

  private AttemptPendingAnalysisRepository repository;
  private ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher;

  private TestAttemptAnalyzer attemptAnalyzer;

  private static final String TEST_SENDER_NAME = "test";

  private static class TestAttemptAnalyzer extends AbstractAttemptAnalyzer {

    private CompletableFuture<Optional<AttemptAnalysis>> mockAnalysis;

    protected TestAttemptAnalyzer(final AttemptPendingAnalysisRepository repository,
        final ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher) {

      super(repository, attemptAnalyzedEventPublisher);
    }

    @Override
    protected String getSenderName() {
      return TEST_SENDER_NAME;
    }

    @Override
    protected CompletableFuture<Optional<AttemptAnalysis>> analyzeAttempt(final AttemptPendingAnalysis attemptPendingAnalysis) {
      return mockAnalysis;
    }

    public void setMockAnalysis(final CompletableFuture<Optional<AttemptAnalysis>> mockAnalysis) {
      this.mockAnalysis = mockAnalysis;
    }
  }

  @BeforeEach
  void setUp() {
    repository = mock(AttemptPendingAnalysisRepository.class);

    //noinspection unchecked
    attemptAnalyzedEventPublisher = mock(ApplicationEventPublisher.class);

    final TaskScheduler taskScheduler = mock(TaskScheduler.class);

    //noinspection unchecked
    when(taskScheduler.scheduleWithFixedDelay(any(), any(), any())).thenReturn(mock(ScheduledFuture.class));

    attemptAnalyzer = new TestAttemptAnalyzer(repository, attemptAnalyzedEventPublisher);
  }

  @Test
  void analyzeAttempts() {
    final String remoteId = RandomStringUtils.randomAlphabetic(16);

    final AttemptPendingAnalysis attemptPendingAnalysis = AttemptPendingAnalysis.newBuilder()
        .setSenderName(TEST_SENDER_NAME)
        .setRemoteId(remoteId)
        .build();

    final AttemptAnalysis attemptAnalysis = new AttemptAnalysis(
        Optional.of(new Money(new BigDecimal("0.1"), Currency.getInstance("USD"))),
        Optional.of("001"),
        Optional.of("002"));

    when(repository.getBySender(TEST_SENDER_NAME)).thenReturn(Mono.just(attemptPendingAnalysis));
    attemptAnalyzer.setMockAnalysis(CompletableFuture.completedFuture(Optional.of(attemptAnalysis)));

    attemptAnalyzer.analyzeAttempts();

    verify(repository).remove(TEST_SENDER_NAME, remoteId);
    verify(attemptAnalyzedEventPublisher).publishEvent(new AttemptAnalyzedEvent(attemptPendingAnalysis, attemptAnalysis));
  }

  @Test
  void analyzeAttemptsNotAvailable() {
    final AttemptPendingAnalysis attemptPendingAnalysis = AttemptPendingAnalysis.newBuilder()
        .setSenderName(TEST_SENDER_NAME)
        .setRemoteId(RandomStringUtils.randomAlphabetic(16))
        .build();

    when(repository.getBySender(TEST_SENDER_NAME)).thenReturn(Mono.just(attemptPendingAnalysis));
    attemptAnalyzer.setMockAnalysis(CompletableFuture.completedFuture(Optional.empty()));

    attemptAnalyzer.analyzeAttempts();

    verify(repository, never()).remove(any(), any());
    verify(attemptAnalyzedEventPublisher, never()).publishEvent(any());
  }

  @Test
  void analyzeAttemptsError() {
    final AttemptPendingAnalysis attemptPendingAnalysis = AttemptPendingAnalysis.newBuilder()
        .setSenderName(TEST_SENDER_NAME)
        .setRemoteId(RandomStringUtils.randomAlphabetic(16))
        .build();

    when(repository.getBySender(TEST_SENDER_NAME)).thenReturn(Mono.just(attemptPendingAnalysis));
    attemptAnalyzer.setMockAnalysis(CompletableFuture.failedFuture(new IOException("OH NO")));

    attemptAnalyzer.analyzeAttempts();

    verify(repository, never()).remove(any(), any());
    verify(attemptAnalyzedEventPublisher, never()).publishEvent(any());
  }
}
