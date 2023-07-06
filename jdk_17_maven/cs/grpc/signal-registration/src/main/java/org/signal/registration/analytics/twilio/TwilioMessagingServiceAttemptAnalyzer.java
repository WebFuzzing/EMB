/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.twilio;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.analytics.AbstractAttemptAnalyzer;
import org.signal.registration.analytics.AttemptAnalysis;
import org.signal.registration.analytics.AttemptAnalyzedEvent;
import org.signal.registration.analytics.AttemptPendingAnalysis;
import org.signal.registration.analytics.AttemptPendingAnalysisRepository;
import org.signal.registration.analytics.Money;
import org.signal.registration.sender.twilio.classic.TwilioMessagingServiceSmsSender;

/**
 * Analyzes verification attempts from {@link TwilioMessagingServiceSmsSender}.
 */
@Singleton
class TwilioMessagingServiceAttemptAnalyzer extends AbstractAttemptAnalyzer {

  private final TwilioRestClient twilioRestClient;

  protected TwilioMessagingServiceAttemptAnalyzer(final AttemptPendingAnalysisRepository repository,
      final ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher,
      final TwilioRestClient twilioRestClient) {

    super(repository, attemptAnalyzedEventPublisher);

    this.twilioRestClient = twilioRestClient;
  }

  @Override
  protected String getSenderName() {
    return TwilioMessagingServiceSmsSender.SENDER_NAME;
  }

  @Override
  @Scheduled(fixedDelay = "${analytics.twilio.sms.analysis-interval:4h}")
  protected void analyzeAttempts() {
    super.analyzeAttempts();
  }

  @Override
  protected CompletableFuture<Optional<AttemptAnalysis>> analyzeAttempt(final AttemptPendingAnalysis attemptPendingAnalysis) {
    return Message.fetcher(attemptPendingAnalysis.getRemoteId()).fetchAsync(twilioRestClient)
        .thenApply(message -> StringUtils.isNotBlank(message.getPrice()) && message.getPriceUnit() != null
            ? Optional.of(
            new AttemptAnalysis(Optional.of(new Money(new BigDecimal(message.getPrice()).negate(),
                Currency.getInstance(message.getPriceUnit().getCurrencyCode().toUpperCase(Locale.ROOT)))),
                Optional.empty(),
                Optional.empty()))
            : Optional.empty());
  }
}
