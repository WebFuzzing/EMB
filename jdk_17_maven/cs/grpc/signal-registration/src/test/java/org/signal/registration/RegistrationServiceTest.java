/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.ByteString;
import io.micronaut.context.event.ApplicationEventPublisher;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.ratelimit.RateLimitExceededException;
import org.signal.registration.ratelimit.RateLimiter;
import org.signal.registration.rpc.RegistrationSessionMetadata;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.SenderRejectedRequestException;
import org.signal.registration.sender.SenderSelectionStrategy;
import org.signal.registration.sender.VerificationCodeSender;
import org.signal.registration.session.MemorySessionRepository;
import org.signal.registration.session.RegistrationAttempt;
import org.signal.registration.session.RegistrationSession;
import org.signal.registration.session.SessionMetadata;
import org.signal.registration.session.SessionNotFoundException;
import org.signal.registration.session.SessionRepository;
import org.signal.registration.util.CompletionExceptions;
import org.signal.registration.util.UUIDUtil;

class RegistrationServiceTest {

  private RegistrationService registrationService;

  private VerificationCodeSender sender;
  private SessionRepository sessionRepository;
  private RateLimiter<Phonenumber.PhoneNumber> sessionCreationRateLimiter;
  private RateLimiter<RegistrationSession> sendSmsVerificationCodeRateLimiter;
  private RateLimiter<RegistrationSession> sendVoiceVerificationCodeRateLimiter;
  private RateLimiter<RegistrationSession> checkVerificationCodeRateLimiter;
  private Clock clock;

  private static final Phonenumber.PhoneNumber PHONE_NUMBER = PhoneNumberUtil.getInstance().getExampleNumber("US");
  private static final String SENDER_NAME = "mock-sender";
  private static final Duration SESSION_TTL = Duration.ofSeconds(17);
  private static final String VERIFICATION_CODE = "654321";
  private static final byte[] VERIFICATION_CODE_BYTES = VERIFICATION_CODE.getBytes(StandardCharsets.UTF_8);
  private static final List<Locale.LanguageRange> LANGUAGE_RANGES = Locale.LanguageRange.parse("en,de");
  private static final ClientType CLIENT_TYPE = ClientType.UNKNOWN;
  private static final SessionMetadata SESSION_METADATA = SessionMetadata.newBuilder().build();
  private static final Instant CURRENT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

  @BeforeEach
  void setUp() {
    sender = mock(VerificationCodeSender.class);
    when(sender.getName()).thenReturn(SENDER_NAME);
    when(sender.getAttemptTtl()).thenReturn(SESSION_TTL);

    clock = mock(Clock.class);
    when(clock.instant()).thenReturn(CURRENT_TIME);
    when(clock.millis()).thenReturn(CURRENT_TIME.toEpochMilli());

    //noinspection unchecked
    sessionRepository = spy(new MemorySessionRepository(mock(ApplicationEventPublisher.class), clock));

    final SenderSelectionStrategy senderSelectionStrategy = mock(SenderSelectionStrategy.class);
    when(senderSelectionStrategy.chooseVerificationCodeSender(any(), any(), any(), any(), any())).thenReturn(sender);

    //noinspection unchecked
    sessionCreationRateLimiter = mock(RateLimiter.class);
    when(sessionCreationRateLimiter.checkRateLimit(any())).thenReturn(CompletableFuture.completedFuture(null));

    //noinspection unchecked
    sendSmsVerificationCodeRateLimiter = mock(RateLimiter.class);
    when(sendSmsVerificationCodeRateLimiter.checkRateLimit(any())).thenReturn(CompletableFuture.completedFuture(null));
    when(sendSmsVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(Optional.of(CURRENT_TIME)));

    //noinspection unchecked
    sendVoiceVerificationCodeRateLimiter = mock(RateLimiter.class);
    when(sendVoiceVerificationCodeRateLimiter.checkRateLimit(any())).thenReturn(CompletableFuture.completedFuture(null));
    when(sendVoiceVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(Optional.of(CURRENT_TIME)));

    //noinspection unchecked
    checkVerificationCodeRateLimiter = mock(RateLimiter.class);
    when(checkVerificationCodeRateLimiter.checkRateLimit(any())).thenReturn(CompletableFuture.completedFuture(null));
    when(checkVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(Optional.of(CURRENT_TIME)));

    registrationService = new RegistrationService(senderSelectionStrategy,
        sessionRepository,
        sessionCreationRateLimiter,
        sendSmsVerificationCodeRateLimiter,
        sendVoiceVerificationCodeRateLimiter,
        checkVerificationCodeRateLimiter,
        List.of(sender),
        clock);
  }

  @Test
  void createSession() {
    final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();

    assertEquals(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164),
        session.getPhoneNumber());

    assertTrue(session.getExpirationEpochMillis() > CURRENT_TIME.toEpochMilli());
    assertFalse(session.getId().isEmpty());
  }

  @Test
  void createSessionRateLimited() {
    final RateLimitExceededException rateLimitExceededException = new RateLimitExceededException(Duration.ZERO);

    when(sessionCreationRateLimiter.checkRateLimit(any()))
        .thenReturn(CompletableFuture.failedFuture(rateLimitExceededException));

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join());

    assertEquals(rateLimitExceededException, CompletionExceptions.unwrap(completionException));
    verify(sessionRepository, never()).createSession(any(), any(), any());
  }

  @Test
  void sendVerificationCode() {
    final String remoteId = UUID.randomUUID().toString();

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());
    }

    when(sender.sendVerificationCode(MessageTransport.SMS, PHONE_NUMBER, LANGUAGE_RANGES, CLIENT_TYPE))
        .thenReturn(CompletableFuture.completedFuture(new AttemptData(Optional.of(remoteId), VERIFICATION_CODE_BYTES)));

    final RegistrationSession session =
        registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();

    verify(sender).sendVerificationCode(MessageTransport.SMS, PHONE_NUMBER, LANGUAGE_RANGES, CLIENT_TYPE);
    verify(sendSmsVerificationCodeRateLimiter).checkRateLimit(any());
    verify(sendVoiceVerificationCodeRateLimiter, never()).checkRateLimit(any());
    verify(sessionRepository).updateSession(eq(sessionId), any());

    assertEquals(1, session.getRegistrationAttemptsCount());
    assertEquals(remoteId, session.getRegistrationAttempts(0).getRemoteId());
    assertEquals(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS,
        session.getRegistrationAttemptsList().get(0).getMessageTransport());
  }

  @Test
  void sendVerificationCodeSmsRateLimited() {
    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());
    }

    when(sendSmsVerificationCodeRateLimiter.checkRateLimit(any()))
        .thenReturn(CompletableFuture.failedFuture(new RateLimitExceededException(null, null)));

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join());

    assertTrue(CompletionExceptions.unwrap(completionException) instanceof RateLimitExceededException);

    verify(sender, never()).sendVerificationCode(any(), any(), any(), any());
    verify(sendSmsVerificationCodeRateLimiter).checkRateLimit(any());
    verify(sendVoiceVerificationCodeRateLimiter, never()).checkRateLimit(any());
    verify(sessionRepository, never()).updateSession(any(), any());
  }

  @Test
  void sendVerificationCodeVoiceRateLimited() {
    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());
    }

    when(sendVoiceVerificationCodeRateLimiter.checkRateLimit(any()))
        .thenReturn(CompletableFuture.failedFuture(new RateLimitExceededException(null, null)));

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> registrationService.sendVerificationCode(MessageTransport.VOICE, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join());

    assertTrue(CompletionExceptions.unwrap(completionException) instanceof RateLimitExceededException);

    verify(sender, never()).sendVerificationCode(any(), any(), any(), any());
    verify(sendSmsVerificationCodeRateLimiter, never()).checkRateLimit(any());
    verify(sendVoiceVerificationCodeRateLimiter).checkRateLimit(any());
    verify(sessionRepository, never()).updateSession(any(), any());
  }

  @Test
  void registrationAttempts() {
    final String firstVerificationCode = "123456";
    final String secondVerificationCode = "234567";

    when(sender.sendVerificationCode(any(), eq(PHONE_NUMBER), eq(LANGUAGE_RANGES), eq(CLIENT_TYPE)))
        .thenReturn(CompletableFuture.completedFuture(new AttemptData(Optional.of("first"), firstVerificationCode.getBytes(StandardCharsets.UTF_8))))
        .thenReturn(CompletableFuture.completedFuture(new AttemptData(Optional.of("second"), secondVerificationCode.getBytes(StandardCharsets.UTF_8))));

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());
    }

    {
      final RegistrationSession session =
          registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, null, LANGUAGE_RANGES, CLIENT_TYPE).join();

      final ByteString expectedSenderData = ByteString.copyFromUtf8(firstVerificationCode);

      assertEquals(1, session.getRegistrationAttemptsList().size());

      final RegistrationAttempt firstAttempt = session.getRegistrationAttempts(0);
      assertEquals(sender.getName(), firstAttempt.getSenderName());
      assertEquals(CURRENT_TIME.toEpochMilli(), firstAttempt.getTimestampEpochMillis());
      assertEquals(expectedSenderData, firstAttempt.getSenderData());
      assertEquals(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS, firstAttempt.getMessageTransport());
    }

    final Instant future = CURRENT_TIME.plus(SESSION_TTL.dividedBy(2));
    when(clock.instant()).thenReturn(future);
    when(clock.millis()).thenReturn(future.toEpochMilli());

    {
      final RegistrationSession session =
          registrationService.sendVerificationCode(MessageTransport.VOICE, sessionId, null, LANGUAGE_RANGES, CLIENT_TYPE).join();

      final ByteString expectedSenderData = ByteString.copyFromUtf8(secondVerificationCode);

      assertEquals(2, session.getRegistrationAttemptsList().size());

      final RegistrationAttempt secondAttempt = session.getRegistrationAttempts(1);
      assertEquals(sender.getName(), secondAttempt.getSenderName());
      assertEquals(future.toEpochMilli(), secondAttempt.getTimestampEpochMillis());
      assertEquals(expectedSenderData, secondAttempt.getSenderData());
      assertEquals(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_VOICE, secondAttempt.getMessageTransport());
    }
  }

  @Test
  void checkVerificationCode() {
    final AttemptData attemptData = new AttemptData(Optional.of("test"), VERIFICATION_CODE_BYTES);

    when(sender.sendVerificationCode(MessageTransport.SMS, PHONE_NUMBER, LANGUAGE_RANGES, CLIENT_TYPE))
        .thenReturn(CompletableFuture.completedFuture(attemptData));

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());

      registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();
    }

    when(sender.checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES))
        .thenReturn(CompletableFuture.completedFuture(true));

    final RegistrationSession session = registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join();

    assertEquals(VERIFICATION_CODE, session.getVerifiedCode());
    assertEquals(1, session.getCheckCodeAttempts());
    assertEquals(CURRENT_TIME.toEpochMilli(), session.getLastCheckCodeAttemptEpochMillis());

    verify(sender).checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES);
  }

  @Test
  void checkVerificationCodeResend() {
    final AttemptData attemptData = new AttemptData(Optional.of("test"), VERIFICATION_CODE_BYTES);

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());
    }

    when(sender.sendVerificationCode(MessageTransport.SMS, PHONE_NUMBER, LANGUAGE_RANGES, CLIENT_TYPE))
        .thenReturn(CompletableFuture.completedFuture(attemptData));

    registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();

    when(sender.checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES))
        .thenReturn(CompletableFuture.completedFuture(false));

    {
      final RegistrationSession session =
          registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join();

      assertTrue(StringUtils.isBlank(session.getVerifiedCode()));
      assertEquals(1, session.getCheckCodeAttempts());
      assertEquals(CURRENT_TIME.toEpochMilli(), session.getLastCheckCodeAttemptEpochMillis());
    }

    {
      final RegistrationSession session =
          registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();

      assertTrue(StringUtils.isBlank(session.getVerifiedCode()));
      assertEquals(0, session.getCheckCodeAttempts());
      assertEquals(0, session.getLastCheckCodeAttemptEpochMillis());
    }

    verify(sender).checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES);
  }

  @Test
  void checkVerificationCodeSessionNotFound() {
    final UUID sessionId = UUID.randomUUID();

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join());

    assertTrue(CompletionExceptions.unwrap(completionException) instanceof SessionNotFoundException);

    verify(sessionRepository).getSession(sessionId);
    verify(sender, never()).checkVerificationCode(any(), any());
    verify(sessionRepository, never()).updateSession(any(), any());
  }

  @Test
  void checkVerificationCodePreviouslyVerified() {
    final UUID sessionId = UUID.randomUUID();

    when(sessionRepository.getSession(sessionId))
        .thenReturn(CompletableFuture.completedFuture(
            RegistrationSession.newBuilder()
                .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164))
                .setVerifiedCode(VERIFICATION_CODE)
                .build()));

    assertEquals(VERIFICATION_CODE, registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join().getVerifiedCode());

    verify(sessionRepository).getSession(sessionId);
    verify(sender, never()).checkVerificationCode(any(), any());
    verify(sessionRepository, never()).updateSession(any(), any());
  }

  @Test
  void checkVerificationCodeRateLimited() {
    final UUID sessionId = UUID.randomUUID();

    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164))
        .addRegistrationAttempts(RegistrationAttempt.newBuilder()
            .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setSenderName(SENDER_NAME)
            .setSenderData(ByteString.copyFrom(VERIFICATION_CODE_BYTES))
            .setExpirationEpochMillis(CURRENT_TIME.toEpochMilli() + 1)
            .build())
        .build();

    when(sessionRepository.getSession(sessionId))
        .thenReturn(CompletableFuture.completedFuture(session));

    final Duration retryAfterDuration = Duration.ofMinutes(17);

    when(checkVerificationCodeRateLimiter.checkRateLimit(session))
        .thenReturn(CompletableFuture.failedFuture(new RateLimitExceededException(retryAfterDuration, session)));

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join());

    final RateLimitExceededException rateLimitExceededException =
        (RateLimitExceededException) CompletionExceptions.unwrap(completionException);

    assertEquals(Optional.of(session), rateLimitExceededException.getRegistrationSession());
    assertEquals(Optional.of(retryAfterDuration), rateLimitExceededException.getRetryAfterDuration());

    verify(sender, never()).checkVerificationCode(any(), any());
    verify(sessionRepository, never()).updateSession(any(), any());
  }

  @Test
  void checkRegistrationCodeAttemptExpired() {
    final UUID sessionId = UUID.randomUUID();

    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164))
        .addRegistrationAttempts(RegistrationAttempt.newBuilder()
            .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setSenderName(SENDER_NAME)
            .setSenderData(ByteString.copyFrom(VERIFICATION_CODE_BYTES))
            .setExpirationEpochMillis(CURRENT_TIME.toEpochMilli() - 1)
            .build())
        .build();

    when(sessionRepository.getSession(sessionId))
        .thenReturn(CompletableFuture.completedFuture(session));

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join());

    assertTrue(CompletionExceptions.unwrap(completionException) instanceof AttemptExpiredException);

    verify(sessionRepository).getSession(sessionId);
    verify(sender, never()).checkVerificationCode(any(), any());
    verify(sessionRepository, never()).updateSession(any(), any());
  }

  @Test
  void legacyCheckVerificationCode() {
    final AttemptData attemptData = new AttemptData(Optional.of("test"), VERIFICATION_CODE_BYTES);

    when(sender.sendVerificationCode(MessageTransport.SMS, PHONE_NUMBER, LANGUAGE_RANGES, CLIENT_TYPE))
        .thenReturn(CompletableFuture.completedFuture(attemptData));

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());

      registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();
    }

    when(sender.checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES))
        .thenReturn(CompletableFuture.completedFuture(true));

    assertTrue(registrationService.legacyCheckVerificationCode(sessionId, VERIFICATION_CODE).join());

    final RegistrationSession session = registrationService.getRegistrationSession(sessionId).join();

    assertEquals(VERIFICATION_CODE, session.getVerifiedCode());
    assertEquals(1, session.getCheckCodeAttempts());
    assertEquals(CURRENT_TIME.toEpochMilli(), session.getLastCheckCodeAttemptEpochMillis());

    verify(sender).checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES);
  }

  @Test
  void legacyCheckVerificationCodeIncorrectCodeAfterCorrectCode() {
    final AttemptData attemptData = new AttemptData(Optional.of("test"), VERIFICATION_CODE_BYTES);

    when(sender.sendVerificationCode(MessageTransport.SMS, PHONE_NUMBER, LANGUAGE_RANGES, CLIENT_TYPE))
        .thenReturn(CompletableFuture.completedFuture(attemptData));

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());

      registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();
    }

    when(sender.checkVerificationCode(any(), any()))
        .thenReturn(CompletableFuture.completedFuture(false));

    when(sender.checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES))
        .thenReturn(CompletableFuture.completedFuture(true));

    assertTrue(registrationService.legacyCheckVerificationCode(sessionId, VERIFICATION_CODE).join());

    final Instant secondCheckTime = CURRENT_TIME.plusSeconds(7);

    when(clock.instant()).thenReturn(secondCheckTime);
    when(clock.millis()).thenReturn(secondCheckTime.toEpochMilli());

    assertFalse(registrationService.legacyCheckVerificationCode(sessionId, VERIFICATION_CODE + "-incorrect").join());

    final RegistrationSession session = registrationService.getRegistrationSession(sessionId).join();

    assertEquals(VERIFICATION_CODE, session.getVerifiedCode());
    assertEquals(2, session.getCheckCodeAttempts());
    assertEquals(secondCheckTime.toEpochMilli(), session.getLastCheckCodeAttemptEpochMillis());

    verify(sender).checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES);
  }

  @ParameterizedTest
  @MethodSource
  void getNextActionTimes(final RegistrationSession session,
      final boolean allowSms,
      final boolean allowVoiceCall,
      final boolean allowCodeCheck,
      final boolean expectNextSms,
      final boolean expectNextVoiceCall,
      final boolean expectNextCodeCheck) {

    final long nextSmsSeconds = 17;
    final long nextVoiceCallSeconds = 19;
    final long nextCodeCheckSeconds = 23;

    when(sendSmsVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(allowSms
            ? Optional.of(CURRENT_TIME.plusSeconds(nextSmsSeconds))
            : Optional.empty()));

    when(sendVoiceVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(allowVoiceCall
            ? Optional.of(CURRENT_TIME.plusSeconds(nextVoiceCallSeconds))
            : Optional.empty()));

    when(checkVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(allowCodeCheck
            ? Optional.of(CURRENT_TIME.plusSeconds(nextCodeCheckSeconds))
            : Optional.empty()));

    final RegistrationService.NextActionTimes nextActionTimes =
        registrationService.getNextActionTimes(session);

    assertEquals(expectNextSms ? Optional.of(CURRENT_TIME.plusSeconds(nextSmsSeconds)) : Optional.empty(),
        nextActionTimes.nextSms());

    assertEquals(expectNextVoiceCall ? Optional.of(CURRENT_TIME.plusSeconds(nextVoiceCallSeconds)) : Optional.empty(),
        nextActionTimes.nextVoiceCall());

    assertEquals(expectNextCodeCheck ? Optional.of(CURRENT_TIME.plusSeconds(nextCodeCheckSeconds)) : Optional.empty(),
        nextActionTimes.nextCodeCheck());
  }

  @ParameterizedTest
  @MethodSource("getNextActionTimes")
  void buildSessionMetadata(final RegistrationSession session,
      final boolean allowSms,
      final boolean allowVoiceCall,
      final boolean allowCodeCheck,
      final boolean expectNextSms,
      final boolean expectNextVoiceCall,
      final boolean expectNextCodeCheck) {

    final long nextSmsSeconds = 17;
    final long nextVoiceCallSeconds = 19;
    final long nextCodeCheckSeconds = 23;

    when(sendSmsVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(allowSms
            ? Optional.of(CURRENT_TIME.plusSeconds(nextSmsSeconds))
            : Optional.empty()));

    when(sendVoiceVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(allowVoiceCall
            ? Optional.of(CURRENT_TIME.plusSeconds(nextVoiceCallSeconds))
            : Optional.empty()));

    when(checkVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(allowCodeCheck
            ? Optional.of(CURRENT_TIME.plusSeconds(nextCodeCheckSeconds))
            : Optional.empty()));

    final RegistrationSessionMetadata sessionMetadata =
        registrationService.buildSessionMetadata(session);

    assertEquals(session.getId(), sessionMetadata.getSessionId());
    assertEquals(
        Long.parseLong(StringUtils.removeStart(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164), "+")),
        sessionMetadata.getE164());

    assertEquals(StringUtils.isNotBlank(session.getVerifiedCode()), sessionMetadata.getVerified());
    assertEquals(expectNextSms, sessionMetadata.getMayRequestSms());
    assertEquals(expectNextSms ? nextSmsSeconds : 0, sessionMetadata.getNextSmsSeconds());
    assertEquals(expectNextVoiceCall, sessionMetadata.getMayRequestVoiceCall());
    assertEquals(expectNextVoiceCall ? nextVoiceCallSeconds : 0, sessionMetadata.getNextVoiceCallSeconds());
    assertEquals(expectNextCodeCheck, sessionMetadata.getMayCheckCode());
    assertEquals(expectNextCodeCheck ? nextCodeCheckSeconds : 0, sessionMetadata.getNextCodeCheckSeconds());
  }

  private static Stream<Arguments> getNextActionTimes() {
    return Stream.of(
        // Fresh session; unverified and no codes sent
        Arguments.of(getBaseSessionBuilder().build(),
            true, true, true,
            true, false, false),

        // Unverified session with an initial SMS sent
        Arguments.of(getBaseSessionBuilder()
                .addRegistrationAttempts(RegistrationAttempt.newBuilder()
                    .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
                    .setExpirationEpochMillis(CURRENT_TIME.plusSeconds(60).toEpochMilli())
                    .build())
                .build(),
            true, true, true,
            true, true, true),

        // Unverified session with an initial SMS sent, but the attempt has expired
        Arguments.of(getBaseSessionBuilder()
                .addRegistrationAttempts(RegistrationAttempt.newBuilder()
                    .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
                    .setExpirationEpochMillis(CURRENT_TIME.minusSeconds(60).toEpochMilli())
                    .build())
                .build(),
            true, true, true,
            true, true, false),

        // Unverified session with an initial SMS sent, but checks for the attempt have been exhausted
        Arguments.of(getBaseSessionBuilder()
                .addRegistrationAttempts(RegistrationAttempt.newBuilder()
                    .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
                    .setExpirationEpochMillis(CURRENT_TIME.plusSeconds(60).toEpochMilli())
                    .build())
                .build(),
            true, true, false,
            true, true, false),

        // Unverified session with SMS attempts exhausted
        Arguments.of(getBaseSessionBuilder()
                .addRegistrationAttempts(RegistrationAttempt.newBuilder()
                    .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
                    .setExpirationEpochMillis(CURRENT_TIME.plusSeconds(60).toEpochMilli())
                    .build())
                .build(),
            false, true, true,
            false, true, true),

        // Unverified session with voice calls exhausted
        Arguments.of(getBaseSessionBuilder()
                .addRegistrationAttempts(RegistrationAttempt.newBuilder()
                    .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
                    .setExpirationEpochMillis(CURRENT_TIME.plusSeconds(60).toEpochMilli())
                    .build())
                .build(),
            true, false, true,
            true, false, true),

        // Verified session
        Arguments.of(getBaseSessionBuilder()
                .addRegistrationAttempts(RegistrationAttempt.newBuilder()
                    .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
                    .build())
                .setVerifiedCode("123456")
                .build(),
            true, true, true,
            false, false, false)
    );
  }

  private static RegistrationSession.Builder getBaseSessionBuilder() {
    return RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164));
  }

  @Test
  void buildSessionMetadataActionInPast() {
    when(sendSmsVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(Optional.of(CURRENT_TIME.minusSeconds(17))));

    when(sendVoiceVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(Optional.of(CURRENT_TIME.minusSeconds(19))));

    when(checkVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(Optional.of(CURRENT_TIME.minusSeconds(23))));

    final RegistrationSession session = getBaseSessionBuilder()
        .setCreatedEpochMillis(CURRENT_TIME.toEpochMilli())
        .addRegistrationAttempts(RegistrationAttempt.newBuilder()
            .setMessageTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setTimestampEpochMillis(CURRENT_TIME.toEpochMilli())
            .setExpirationEpochMillis(CURRENT_TIME.plusSeconds(600).toEpochMilli())
            .build())
        .build();

    final RegistrationSessionMetadata metadata = registrationService.buildSessionMetadata(session);

    assertTrue(metadata.getMayRequestSms());
    assertEquals(0, metadata.getNextSmsSeconds());

    assertTrue(metadata.getMayRequestVoiceCall());
    assertEquals(0, metadata.getNextVoiceCallSeconds());

    assertTrue(metadata.getMayCheckCode());
    assertEquals(0, metadata.getNextCodeCheckSeconds());
  }

  @Test
  void checkVerificationCodeSenderException() {
    final AttemptData attemptData = new AttemptData(Optional.of("test"), VERIFICATION_CODE_BYTES);

    when(sender.sendVerificationCode(any(), any(), any(), any()))
        .thenReturn(CompletableFuture.completedFuture(attemptData));

    final UUID sessionId;
    {
      final RegistrationSession session = registrationService.createRegistrationSession(PHONE_NUMBER, SESSION_METADATA).join();
      sessionId = UUIDUtil.uuidFromByteString(session.getId());

      registrationService.sendVerificationCode(MessageTransport.SMS, sessionId, SENDER_NAME, LANGUAGE_RANGES, CLIENT_TYPE).join();
    }

    when(sender.checkVerificationCode(VERIFICATION_CODE, VERIFICATION_CODE_BYTES))
        .thenReturn(CompletableFuture.failedFuture(new SenderRejectedRequestException(new RuntimeException("OH NO"))));

    final RegistrationSession session = registrationService.checkVerificationCode(sessionId, VERIFICATION_CODE).join();

    assertTrue(StringUtils.isBlank(session.getVerifiedCode()));
    assertEquals(1, session.getCheckCodeAttempts());
    assertEquals(CURRENT_TIME.toEpochMilli(), session.getLastCheckCodeAttemptEpochMillis());
  }

  @ParameterizedTest
  @MethodSource
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  void getSessionExpiration(final Instant sessionCreation,
      final boolean verified,
      final Instant lastCodeCheck,
      final Optional<Instant> nextSms,
      final List<Instant> attemptExpirations,
      final Instant expectedExpiration) {

    when(sendSmsVerificationCodeRateLimiter.getTimeOfNextAction(any()))
        .thenReturn(CompletableFuture.completedFuture(nextSms));

    final RegistrationSession.Builder sessionBuilder = RegistrationSession.newBuilder()
        .setCreatedEpochMillis(sessionCreation.toEpochMilli())
        .setLastCheckCodeAttemptEpochMillis(lastCodeCheck.toEpochMilli());

    if (verified) {
      sessionBuilder.setVerifiedCode("verified");
    }

    attemptExpirations.stream()
        .map(attemptExpiration -> RegistrationAttempt.newBuilder()
            .setExpirationEpochMillis(attemptExpiration.toEpochMilli())
            .build())
        .forEach(sessionBuilder::addRegistrationAttempts);

    assertEquals(expectedExpiration, registrationService.getSessionExpiration(sessionBuilder.build()));
  }

  private static Stream<Arguments> getSessionExpiration() {
    return Stream.of(
        // Session verified right now
        Arguments.of(CURRENT_TIME,
            true,
            CURRENT_TIME,
            Optional.empty(),
            List.of(),
            CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION)),

        // Verification code never checked, ready to send an SMS in two minutes
        Arguments.of(CURRENT_TIME.minusSeconds(600),
            false,
            Instant.ofEpochMilli(0),
            Optional.of(CURRENT_TIME.plusSeconds(120)),
            List.of(),
            CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION).plus(Duration.ofMinutes(2))),

        // Verification code never checked, not allowed to request another SMS
        Arguments.of(CURRENT_TIME.minusSeconds(600),
            false,
            Instant.ofEpochMilli(0),
            Optional.empty(),
            List.of(CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION).plus(Duration.ofMinutes(3))),
            CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION).plus(Duration.ofMinutes(3))),

        // Verification code never checked; two recent registration attempts
        Arguments.of(CURRENT_TIME.minusSeconds(600),
            false,
            Instant.ofEpochMilli(0),
            Optional.of(CURRENT_TIME.plusSeconds(120)),
            List.of(
                CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION).plus(Duration.ofMinutes(3)),
                CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION).plus(Duration.ofMinutes(5))),
            CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION).plus(Duration.ofMinutes(5))),

        // Fresh session with some time elapsed
        Arguments.of(
            CURRENT_TIME.minus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION.dividedBy(2)),
            false,
            Instant.ofEpochMilli(0),
            Optional.of(CURRENT_TIME.minus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION.dividedBy(2))),
            List.of(),
            CURRENT_TIME.plus(RegistrationService.SESSION_TTL_AFTER_LAST_ACTION.dividedBy(2)))
    );
  }
}
