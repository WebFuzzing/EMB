/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micronaut.configuration.metrics.annotation.RequiresMetrics;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.Currency;
import org.signal.registration.Environments;
import org.signal.registration.metrics.MetricsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An attempt price metrics listener listens for analyzed verification attempts and increments a price counter for
 * events that include a price.
 */
@Singleton
@RequiresMetrics
@Requires(env = Environments.ANALYTICS)
class AttemptPriceMetricsListener implements ApplicationEventListener<AttemptAnalyzedEvent> {

  private final MeterRegistry meterRegistry;

  private static final Currency USD = Currency.getInstance("USD");
  private static final BigDecimal ONE_MILLION = new BigDecimal("1e6");

  private static final String ATTEMPT_COUNTER_NAME =
      MetricsUtil.name(AttemptPriceMetricsListener.class, "attemptsAnalyzed");

  private static final String PRICE_COUNTER_NAME =
      MetricsUtil.name(AttemptPriceMetricsListener.class, "attemptPriceMicros");

  private static final Logger logger = LoggerFactory.getLogger(AttemptPriceMetricsListener.class);

  AttemptPriceMetricsListener(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void onApplicationEvent(final AttemptAnalyzedEvent event) {
    event.attemptAnalysis().price().ifPresent(price -> {
      if (USD.equals(price.currency())) {
        final Tags tags = Tags.of(MetricsUtil.SENDER_TAG_NAME, event.attemptPendingAnalysis().getSenderName(),
            MetricsUtil.CLIENT_TYPE_TAG_NAME, MetricsUtil.getClientTypeTagValue(event.attemptPendingAnalysis().getClientType()),
            MetricsUtil.TRANSPORT_TAG_NAME, MetricsUtil.getMessageTransportTagValue(event.attemptPendingAnalysis().getMessageTransport()),
            MetricsUtil.VERIFIED_TAG_NAME, String.valueOf(event.attemptPendingAnalysis().getVerified()),
            MetricsUtil.REGION_CODE_TAG_NAME, event.attemptPendingAnalysis().getRegion(),
            MetricsUtil.COUNTRY_CODE_TAG_NAME, String.valueOf(PhoneNumberUtil.getInstance().getCountryCodeForRegion(event.attemptPendingAnalysis().getRegion())));

        meterRegistry.counter(ATTEMPT_COUNTER_NAME, tags).increment();
        meterRegistry.counter(PRICE_COUNTER_NAME, tags).increment(price.amount().multiply(ONE_MILLION).longValue());
      } else {
        logger.warn("Price provided in non-USD currency ({}) by {}", price.currency(), event.attemptPendingAnalysis().getSenderName());
      }
    });
  }
}
