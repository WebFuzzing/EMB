/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.twilio;

import com.google.common.annotations.VisibleForTesting;
import com.twilio.base.Page;
import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.verify.v2.VerificationAttempt;
import com.twilio.rest.verify.v2.VerificationAttemptReader;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.analytics.AttemptAnalysis;
import org.signal.registration.analytics.AttemptAnalyzedEvent;
import org.signal.registration.analytics.AttemptPendingAnalysisRepository;
import org.signal.registration.analytics.Money;
import org.signal.registration.metrics.MetricsUtil;
import org.signal.registration.sender.twilio.verify.TwilioVerifySender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Analyzes verification attempts from {@link TwilioVerifySender}.
 */
@Singleton
class TwilioVerifyAttemptAnalyzer {

  private final TwilioRestClient twilioRestClient;
  private final AttemptPendingAnalysisRepository repository;

  private final ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher;

  private final String verifyServiceSid;

  private final Counter attemptReadCounter;
  private final Counter attemptAnalyzedCounter;

  private static final String CURRENCY_KEY = "currency";
  private static final String VALUE_KEY = "value";
  private static final String MCC_KEY = "mcc";
  private static final String MNC_KEY = "mnc";

  private static final Duration MAX_ATTEMPT_AGE = Duration.ofDays(2);
  private static final int PAGE_SIZE = 1_000;
  private static final int TOO_MANY_REQUESTS_CODE = 20429;

  private static final Logger logger = LoggerFactory.getLogger(TwilioVerifyAttemptAnalyzer.class);

  public TwilioVerifyAttemptAnalyzer(final TwilioRestClient twilioRestClient,
      final AttemptPendingAnalysisRepository repository,
      final ApplicationEventPublisher<AttemptAnalyzedEvent> attemptAnalyzedEventPublisher,
      @Value("${twilio.verify.service-sid}") final String verifyServiceSid,
      final MeterRegistry meterRegistry) {

    this.twilioRestClient = twilioRestClient;
    this.repository = repository;
    this.attemptAnalyzedEventPublisher = attemptAnalyzedEventPublisher;

    this.verifyServiceSid = verifyServiceSid;

    this.attemptReadCounter = meterRegistry.counter(MetricsUtil.name(getClass(), "attemptRead"));
    this.attemptAnalyzedCounter = meterRegistry.counter(MetricsUtil.name(getClass(), "attemptAnalyzed"));
  }

  @Scheduled(fixedDelay = "${analytics.twilio.verify.analysis-interval:4h}")
  void analyzeAttempts() {
    // While most attempt analyzers fetch a stream of attempts pending analysis from our own repository and resolve them
    // one by one, the rate limits for the Twilio Verifications Attempt API (see
    // https://www.twilio.com/docs/verify/api/list-verification-attempts#rate-limits) prevent us from doing that here.
    // Instead, we fetch verification attempts from the Twilio API using fewer, larger pages and reconcile those against
    // what we have stored locally.
    analyzeAttempts(getVerificationAttempts());
  }

  @VisibleForTesting
  void analyzeAttempts(final Flux<VerificationAttempt> verificationAttempts) {
    verificationAttempts
        .doOnNext(ignored -> attemptReadCounter.increment())
        .filter(verificationAttempt -> verificationAttempt.getPrice() != null
            && verificationAttempt.getPrice().get(VALUE_KEY) != null
            && verificationAttempt.getPrice().get(CURRENCY_KEY) != null)
        .flatMap(verificationAttempt -> Mono.fromFuture(
                repository.getByRemoteIdentifier(TwilioVerifySender.SENDER_NAME, verificationAttempt.getSid()))
            .flatMap(Mono::justOrEmpty)
            .flatMap(attemptPendingAnalysis -> {
              final Money price;

              try {
                price = new Money(new BigDecimal(verificationAttempt.getPrice().get(VALUE_KEY).toString()),
                    Currency.getInstance(
                        verificationAttempt.getPrice().get(CURRENCY_KEY).toString().toUpperCase(Locale.ROOT)));
              } catch (final IllegalArgumentException e) {
                logger.warn("Failed to parse price: {}", verificationAttempt, e);
                return Mono.empty();
              }

              final Optional<Map<String, Object>> maybeChannelData =
                  Optional.ofNullable(verificationAttempt.getChannelData());

              final Optional<String> maybeMcc = maybeChannelData
                  .map(channelData -> channelData.get(MCC_KEY))
                  .map(mcc -> StringUtils.stripToNull(mcc.toString()));

              final Optional<String> maybeMnc = maybeChannelData
                  .map(channelData -> channelData.get(MNC_KEY))
                  .map(mnc -> StringUtils.stripToNull(mnc.toString()));

              return Mono.just(new AttemptAnalyzedEvent(attemptPendingAnalysis,
                  new AttemptAnalysis(Optional.of(price), maybeMcc, maybeMnc)));
            }))
        .doOnNext(analyzedAttempt -> {
          attemptAnalyzedCounter.increment();

          repository.remove(TwilioVerifySender.SENDER_NAME, analyzedAttempt.attemptPendingAnalysis().getRemoteId());
          attemptAnalyzedEventPublisher.publishEvent(analyzedAttempt);
        })
        .blockLast();
  }

  private Flux<VerificationAttempt> getVerificationAttempts() {
    final VerificationAttemptReader reader = VerificationAttempt.reader()
        .setVerifyServiceSid(verifyServiceSid)
        .setDateCreatedAfter(ZonedDateTime.now().minus(MAX_ATTEMPT_AGE))
        .setPageSize(PAGE_SIZE);

    return Flux.from(fetchPageWithBackoff(() -> reader.firstPage(twilioRestClient)))
        .expand(page -> {
          if (page.hasNextPage()) {
            return fetchPageWithBackoff(() -> reader.nextPage(page, twilioRestClient));
          } else {
            return Mono.empty();
          }
        })
        .flatMapIterable(Page::getRecords);
  }

  private Mono<Page<VerificationAttempt>> fetchPageWithBackoff(final Supplier<Page<VerificationAttempt>> pageSupplier) {
    return Mono.fromSupplier(pageSupplier)
        .retryWhen(Retry.backoff(10, Duration.ofMillis(500))
            .filter(throwable -> throwable instanceof ApiException apiException && apiException.getCode() == TOO_MANY_REQUESTS_CODE)
            .maxBackoff(Duration.ofSeconds(8)));
  }
}
