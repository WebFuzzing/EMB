/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import com.google.common.annotations.VisibleForTesting;
import io.micronaut.context.event.ApplicationEventPublisher;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.signal.registration.sender.VerificationCodeSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An abstract attempt analyzer periodically reads attempts pending analysis from a
 * {@link AttemptPendingAnalysisRepository} for a specific {@link org.signal.registration.sender.VerificationCodeSender},
 * and attempts to analyze each attempt individually. When an attempt pending analysis is analyzed successfully, it is
 * removed from the repository and an {@link AttemptAnalyzedEvent} is triggered.
 * <p>
 * Subclasses should call the {@link #analyzeAttempts()} method at regular intervals.
 */
public abstract class AbstractAttemptAnalyzer {

  private final AttemptPendingAnalysisRepository repository;
  private final ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  protected AbstractAttemptAnalyzer(final AttemptPendingAnalysisRepository repository,
      final ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher) {

    this.repository = repository;
    this.attemptAnalyzedEventPublisher = attemptAnalyzedEventPublisher;
  }

  protected void analyzeAttempts() {
    logger.debug("Processing attempts pending analysis");

    Flux.from(repository.getBySender(getSenderName()))
        .flatMap(attemptPendingAnalysis -> Mono.fromFuture(analyzeAttempt(attemptPendingAnalysis)
                .thenApply(maybeAnalysis -> maybeAnalysis.map(analysis ->
                    new AttemptAnalyzedEvent(attemptPendingAnalysis, analysis))))
            .onErrorReturn(Optional.empty()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .subscribe(attemptAnalyzedEvent -> {
          repository.remove(attemptAnalyzedEvent.attemptPendingAnalysis().getSenderName(), attemptAnalyzedEvent.attemptPendingAnalysis().getRemoteId());
          attemptAnalyzedEventPublisher.publishEvent(attemptAnalyzedEvent);
        });
  }

  /**
   * Returns the name of the {@link org.signal.registration.sender.VerificationCodeSender} whose attempts pending
   * analysis will be processed by this analyzer.
   *
   * @return the name of the verification code sender whose attempts pending analysis will be processed by this analyzer
   */
  protected abstract String getSenderName();

  /**
   * Attempts to retrieve additional details (presumably from an external service provider) about an attempt pending
   * analysis.
   *
   * @param attemptPendingAnalysis the attempt for which to retrieve additional details
   *
   * @return an analysis of the attempt or empty if an analysis is not yet available
   *
   * @see VerificationCodeSender#getName()
   */
  protected abstract CompletableFuture<Optional<AttemptAnalysis>> analyzeAttempt(final AttemptPendingAnalysis attemptPendingAnalysis);
}
