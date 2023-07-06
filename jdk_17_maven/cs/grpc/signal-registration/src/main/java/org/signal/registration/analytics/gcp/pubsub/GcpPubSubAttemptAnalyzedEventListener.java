/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.gcp.pubsub;

import com.google.common.annotations.VisibleForTesting;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Singleton;
import org.signal.registration.analytics.AttemptAnalyzedEvent;
import org.signal.registration.metrics.MetricsUtil;
import org.signal.registration.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A GCP pub/sub "analyzed event" listener dispatches messages to a GCP pub/sub topic. Pub/sub messages emitted by this
 * listener use a schema that is compatible with a BigQuery subscriber.
 */
@Singleton
public class GcpPubSubAttemptAnalyzedEventListener implements ApplicationEventListener<AttemptAnalyzedEvent> {

  private final AttemptAnalyzedPubSubMessageClient pubSubClient;
  private final MeterRegistry meterRegistry;

  private static final String COUNTER_NAME =
      MetricsUtil.name(GcpPubSubAttemptAnalyzedEventListener.class, "messageSent");

  private static final BigDecimal ONE_MILLION = new BigDecimal("1e6");

  private static final Logger logger = LoggerFactory.getLogger(GcpPubSubAttemptAnalyzedEventListener.class);

  public GcpPubSubAttemptAnalyzedEventListener(final AttemptAnalyzedPubSubMessageClient pubSubClient,
      final MeterRegistry meterRegistry) {

    this.pubSubClient = pubSubClient;
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void onApplicationEvent(final AttemptAnalyzedEvent event) {
    boolean success = false;

    try {
      pubSubClient.send(buildPubSubMessage(event).toByteArray());
      success = true;
    } catch (final Exception e) {
      logger.warn("Failed to send pub/sub message", e);
    } finally {
      meterRegistry.counter(COUNTER_NAME,
              MetricsUtil.SUCCESS_TAG_NAME, String.valueOf(success),
              MetricsUtil.SENDER_TAG_NAME, event.attemptPendingAnalysis().getSenderName())
          .increment();
    }
  }

  @VisibleForTesting
  static AttemptAnalyzedPubSubMessage buildPubSubMessage(final AttemptAnalyzedEvent event) {
    final AttemptAnalyzedPubSubMessage.Builder pubSubMessageBuilder = AttemptAnalyzedPubSubMessage.newBuilder()
        .setSessionId(UUIDUtil.uuidFromByteString(event.attemptPendingAnalysis().getSessionId()).toString())
        .setAttemptId(event.attemptPendingAnalysis().getAttemptId())
        .setSenderName(event.attemptPendingAnalysis().getSenderName())
        .setMessageTransport(MetricsUtil.getMessageTransportTagValue(event.attemptPendingAnalysis().getMessageTransport()))
        .setClientType(MetricsUtil.getClientTypeTagValue(event.attemptPendingAnalysis().getClientType()))
        .setRegion(event.attemptPendingAnalysis().getRegion())
        .setTimestamp(Instant.ofEpochMilli(event.attemptPendingAnalysis().getTimestampEpochMillis()).toString())
        .setAccountExistsWithE164(event.attemptPendingAnalysis().getAccountExistsWithE164())
        .setVerified(event.attemptPendingAnalysis().getVerified());

    event.attemptAnalysis().price().ifPresent(price -> {
      pubSubMessageBuilder.setPriceMicros(price.amount().multiply(ONE_MILLION).longValue());
      pubSubMessageBuilder.setCurrency(price.currency().getCurrencyCode());
    });

    event.attemptAnalysis().mcc().ifPresent(pubSubMessageBuilder::setSenderMcc);
    event.attemptAnalysis().mnc().ifPresent(pubSubMessageBuilder::setSenderMnc);

    return pubSubMessageBuilder.build();
  }
}
