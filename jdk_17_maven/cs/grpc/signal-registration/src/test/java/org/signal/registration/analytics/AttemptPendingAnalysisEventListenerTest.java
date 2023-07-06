/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.signal.registration.rpc.ClientType;
import org.signal.registration.rpc.MessageTransport;
import org.signal.registration.session.RegistrationAttempt;
import org.signal.registration.session.RegistrationSession;
import org.signal.registration.session.SessionCompletedEvent;
import org.signal.registration.session.SessionMetadata;
import org.signal.registration.util.UUIDUtil;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttemptPendingAnalysisEventListenerTest {

  private AttemptPendingAnalysisRepository repository;
  private AttemptPendingAnalysisEventListener listener;

  @BeforeEach
  void setUp() {
    repository = mock(AttemptPendingAnalysisRepository.class);
    when(repository.store(any())).thenReturn(CompletableFuture.completedFuture(null));

    listener = new AttemptPendingAnalysisEventListener(repository, new SimpleMeterRegistry());
  }

  @Test
  void onApplicationEvent() {
    final UUID sessionId = UUID.randomUUID();
    final Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().getExampleNumber("US");

    final String firstRemoteId = RandomStringUtils.randomAlphabetic(16);
    final String secondRemoteId = RandomStringUtils.randomAlphabetic(16);

    final RegistrationSession registrationSession = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
        .setVerifiedCode(RandomStringUtils.randomNumeric(6))
        .setSessionMetadata(SessionMetadata.newBuilder().setAccountExistsWithE164(true).build())
        .addRegistrationAttempts(buildRegistrationAttempt(firstRemoteId))
        .addRegistrationAttempts(buildRegistrationAttempt(null))
        .addRegistrationAttempts(buildRegistrationAttempt(secondRemoteId))
        .build();

    listener.onApplicationEvent(new SessionCompletedEvent(registrationSession));

    final ArgumentCaptor<AttemptPendingAnalysis> attemptPendingAnalysisCaptor =
        ArgumentCaptor.forClass(AttemptPendingAnalysis.class);

    verify(repository, times(2)).store(attemptPendingAnalysisCaptor.capture());

    {
      final AttemptPendingAnalysis attemptPendingAnalysis = attemptPendingAnalysisCaptor.getAllValues().get(0);
      assertEquals(UUIDUtil.uuidToByteString(sessionId), attemptPendingAnalysis.getSessionId());
      assertEquals(firstRemoteId, attemptPendingAnalysis.getRemoteId());
      assertFalse(attemptPendingAnalysis.getVerified());
      assertEquals("US", attemptPendingAnalysis.getRegion());
    }

    {
      final AttemptPendingAnalysis attemptPendingAnalysis = attemptPendingAnalysisCaptor.getAllValues().get(1);
      assertEquals(UUIDUtil.uuidToByteString(sessionId), attemptPendingAnalysis.getSessionId());
      assertEquals(secondRemoteId, attemptPendingAnalysis.getRemoteId());
      assertTrue(attemptPendingAnalysis.getVerified());
      assertEquals("US", attemptPendingAnalysis.getRegion());
    }
  }

  private static RegistrationAttempt buildRegistrationAttempt(@Nullable String remoteId) {
    final RegistrationAttempt.Builder attemptBuilder = RegistrationAttempt.newBuilder()
        .setTimestampEpochMillis(System.currentTimeMillis())
        .setSenderName("sender")
        .setMessageTransport(ThreadLocalRandom.current().nextBoolean() ? MessageTransport.MESSAGE_TRANSPORT_SMS : MessageTransport.MESSAGE_TRANSPORT_VOICE)
        .setClientType(ThreadLocalRandom.current().nextBoolean() ? ClientType.CLIENT_TYPE_IOS : ClientType.CLIENT_TYPE_ANDROID_WITH_FCM);

    if (remoteId != null) {
      attemptBuilder.setRemoteId(remoteId);
    }

    return attemptBuilder.build();
  }
}
