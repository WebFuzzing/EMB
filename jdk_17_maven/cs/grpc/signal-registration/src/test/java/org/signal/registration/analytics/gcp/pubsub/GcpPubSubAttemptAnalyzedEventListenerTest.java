/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.gcp.pubsub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.analytics.AttemptAnalysis;
import org.signal.registration.analytics.AttemptAnalyzedEvent;
import org.signal.registration.analytics.AttemptPendingAnalysis;
import org.signal.registration.analytics.Money;
import org.signal.registration.rpc.ClientType;
import org.signal.registration.rpc.MessageTransport;
import org.signal.registration.util.UUIDUtil;

class GcpPubSubAttemptAnalyzedEventListenerTest {

  private AttemptAnalyzedPubSubMessageClient pubSubClient;
  private GcpPubSubAttemptAnalyzedEventListener listener;

  private static final BigDecimal ONE_MILLION = new BigDecimal("1e6");

  @BeforeEach
  void setUp() {
    pubSubClient = mock(AttemptAnalyzedPubSubMessageClient.class);
    listener = new GcpPubSubAttemptAnalyzedEventListener(pubSubClient, new SimpleMeterRegistry());
  }

  @Test
  void onApplicationEvent() {
    final UUID sessionId = UUID.randomUUID();
    final int attemptId = 7;
    final String senderName = "test";

    final String region = "XX";
    final long timestamp = System.currentTimeMillis();
    final boolean accountExistsWithE164 = true;
    final boolean verified = false;

    final AttemptPendingAnalysis attemptPendingAnalysis = AttemptPendingAnalysis.newBuilder()
        .setSessionId(UUIDUtil.uuidToByteString(sessionId))
        .setAttemptId(attemptId)
        .setSenderName(senderName)
        .setRemoteId("remote-id")
        .setMessageTransport(MessageTransport.MESSAGE_TRANSPORT_SMS)
        .setClientType(ClientType.CLIENT_TYPE_IOS)
        .setRegion(region)
        .setTimestampEpochMillis(timestamp)
        .setAccountExistsWithE164(accountExistsWithE164)
        .setVerified(verified)
        .build();

    final Money price = new Money(new BigDecimal("0.04"), Currency.getInstance("USD"));
    final String mcc = "017";
    final String mnc = "029";

    final AttemptAnalysis attemptAnalysis = new AttemptAnalysis(
        Optional.of(price),
        Optional.of(mcc),
        Optional.of(mnc));

    final AttemptAnalyzedPubSubMessage expectedPubSubMessage = AttemptAnalyzedPubSubMessage.newBuilder()
        .setSessionId(sessionId.toString())
        .setAttemptId(attemptId)
        .setSenderName(senderName)
        .setMessageTransport("sms")
        .setClientType("ios")
        .setRegion(region)
        .setTimestamp(Instant.ofEpochMilli(timestamp).toString())
        .setAccountExistsWithE164(accountExistsWithE164)
        .setVerified(verified)
        .setPriceMicros(price.amount().multiply(ONE_MILLION).longValueExact())
        .setCurrency(price.currency().getCurrencyCode())
        .setSenderMcc(mcc)
        .setSenderMnc(mnc)
        .build();

    listener.onApplicationEvent(new AttemptAnalyzedEvent(attemptPendingAnalysis, attemptAnalysis));

    verify(pubSubClient).send(expectedPubSubMessage.toByteArray());
  }

  @ParameterizedTest
  @MethodSource
  void buildPubSubMessage(final ClientType clientType,
      final MessageTransport messageTransport,
      final String expectedClientType,
      final String expectedMessageTransport) {

    final UUID sessionId = UUID.randomUUID();
    final int attemptId = 7;
    final String senderName = "test";

    final String region = "XX";
    final long timestamp = System.currentTimeMillis();
    final boolean accountExistsWithE164 = true;
    final boolean verified = false;

    final AttemptPendingAnalysis attemptPendingAnalysis = AttemptPendingAnalysis.newBuilder()
        .setSessionId(UUIDUtil.uuidToByteString(sessionId))
        .setAttemptId(attemptId)
        .setSenderName(senderName)
        .setRemoteId("remote-id")
        .setMessageTransport(messageTransport)
        .setClientType(clientType)
        .setRegion(region)
        .setTimestampEpochMillis(timestamp)
        .setAccountExistsWithE164(accountExistsWithE164)
        .setVerified(verified)
        .build();

    final Money price = new Money(new BigDecimal("0.04"), Currency.getInstance("USD"));
    final String mcc = "017";
    final String mnc = "029";

    final AttemptAnalysis attemptAnalysis = new AttemptAnalysis(
        Optional.of(price),
        Optional.of(mcc),
        Optional.of(mnc));

    final AttemptAnalyzedPubSubMessage expectedPubSubMessage = AttemptAnalyzedPubSubMessage.newBuilder()
        .setSessionId(sessionId.toString())
        .setAttemptId(attemptId)
        .setSenderName(senderName)
        .setMessageTransport(expectedMessageTransport)
        .setClientType(expectedClientType)
        .setRegion(region)
        .setTimestamp(Instant.ofEpochMilli(timestamp).toString())
        .setAccountExistsWithE164(accountExistsWithE164)
        .setVerified(verified)
        .setPriceMicros(price.amount().multiply(ONE_MILLION).longValueExact())
        .setCurrency(price.currency().getCurrencyCode())
        .setSenderMcc(mcc)
        .setSenderMnc(mnc)
        .build();

    assertEquals(expectedPubSubMessage,
        GcpPubSubAttemptAnalyzedEventListener.buildPubSubMessage(new AttemptAnalyzedEvent(attemptPendingAnalysis, attemptAnalysis)));
  }

  private static Stream<Arguments> buildPubSubMessage() {
    return Stream.of(
        Arguments.of(ClientType.CLIENT_TYPE_IOS, MessageTransport.MESSAGE_TRANSPORT_SMS, "ios", "sms"),
        Arguments.of(ClientType.CLIENT_TYPE_ANDROID_WITH_FCM, MessageTransport.MESSAGE_TRANSPORT_SMS, "android-with-fcm", "sms"),
        Arguments.of(ClientType.CLIENT_TYPE_ANDROID_WITHOUT_FCM, MessageTransport.MESSAGE_TRANSPORT_SMS, "android-without-fcm", "sms"),
        Arguments.of(ClientType.CLIENT_TYPE_UNSPECIFIED, MessageTransport.MESSAGE_TRANSPORT_SMS, "unrecognized", "sms"),
        Arguments.of(ClientType.CLIENT_TYPE_IOS, MessageTransport.MESSAGE_TRANSPORT_VOICE, "ios", "voice"),
        Arguments.of(ClientType.CLIENT_TYPE_IOS, MessageTransport.MESSAGE_TRANSPORT_UNSPECIFIED, "ios", "unrecognized")
    );
  }
}
