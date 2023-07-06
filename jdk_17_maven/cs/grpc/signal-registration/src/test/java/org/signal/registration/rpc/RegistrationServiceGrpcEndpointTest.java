/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.rpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.signal.registration.AttemptExpiredException;
import org.signal.registration.NoVerificationCodeSentException;
import org.signal.registration.RegistrationService;
import org.signal.registration.SessionAlreadyVerifiedException;
import org.signal.registration.ratelimit.RateLimitExceededException;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.IllegalSenderArgumentException;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.SenderRejectedRequestException;
import org.signal.registration.session.RegistrationSession;
import org.signal.registration.session.SessionNotFoundException;
import org.signal.registration.util.UUIDUtil;

@MicronautTest
class RegistrationServiceGrpcEndpointTest {

  @MockBean(RegistrationService.class)
  RegistrationService registrationService() {
    final RegistrationService registrationService = mock(RegistrationService.class);

    when(registrationService.buildSessionMetadata(any()))
        .thenAnswer((Answer<RegistrationSessionMetadata>) invocation -> {
          final RegistrationSession session = invocation.getArgument(0, RegistrationSession.class);

          return RegistrationSessionMetadata.newBuilder()
              .setSessionId(session.getId())
              .setVerified(StringUtils.isNotBlank(session.getVerifiedCode()))
              .build();
        });

    return registrationService;
  }

  @Inject
  private RegistrationServiceGrpc.RegistrationServiceBlockingStub blockingStub;

  @Inject
  private RegistrationService registrationService;

  @Test
  void createSession() {
    final long e164 = 18005550123L;
    final UUID sessionId = UUID.randomUUID();

    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber("+" + e164)
        .build();

    when(registrationService.createRegistrationSession(any(), any()))
        .thenReturn(CompletableFuture.completedFuture(session));

    final CreateRegistrationSessionResponse response =
        blockingStub.createSession(CreateRegistrationSessionRequest.newBuilder()
            .setE164(e164)
            .build());

    assertEquals(CreateRegistrationSessionResponse.ResponseCase.SESSION_METADATA, response.getResponseCase());
    assertEquals(UUIDUtil.uuidToByteString(sessionId), response.getSessionMetadata().getSessionId());
  }

  @Test
  void createSessionRateLimited() {
    final Duration retryAfter = Duration.ofSeconds(60);

    when(registrationService.createRegistrationSession(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new RateLimitExceededException(retryAfter)));

    final CreateRegistrationSessionResponse response =
        blockingStub.createSession(CreateRegistrationSessionRequest.newBuilder()
            .setE164(18005550123L)
            .build());

    assertEquals(CreateRegistrationSessionResponse.ResponseCase.ERROR, response.getResponseCase());
    assertEquals(CreateRegistrationSessionErrorType.CREATE_REGISTRATION_SESSION_ERROR_TYPE_RATE_LIMITED, response.getError().getErrorType());
    assertEquals(retryAfter.toSeconds(), response.getError().getRetryAfterSeconds());
  }

  @Test
  void createSessionBadPhoneNumber() {
    final CreateRegistrationSessionResponse response =
        blockingStub.createSession(CreateRegistrationSessionRequest.newBuilder()
            .setE164(12L)
            .build());

    assertEquals(CreateRegistrationSessionResponse.ResponseCase.ERROR, response.getResponseCase());
    assertEquals(CreateRegistrationSessionErrorType.CREATE_REGISTRATION_SESSION_ERROR_TYPE_ILLEGAL_PHONE_NUMBER, response.getError().getErrorType());
  }

  @Test
  void getSessionMetadata() {
    final UUID sessionId = UUID.randomUUID();

    when(registrationService.getRegistrationSession(sessionId))
        .thenReturn(CompletableFuture.completedFuture(RegistrationSession.newBuilder()
            .setId(UUIDUtil.uuidToByteString(sessionId))
            .build()));

    final GetRegistrationSessionMetadataResponse response =
        blockingStub.getSessionMetadata(GetRegistrationSessionMetadataRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(sessionId))
            .build());

    assertEquals(GetRegistrationSessionMetadataResponse.ResponseCase.SESSION_METADATA, response.getResponseCase());
    assertTrue(response.hasSessionMetadata());
  }

  @Test
  void getSessionMetadataNotFound() {
    when(registrationService.getRegistrationSession(any()))
        .thenReturn(CompletableFuture.failedFuture(new SessionNotFoundException()));

    final GetRegistrationSessionMetadataResponse response =
        blockingStub.getSessionMetadata(GetRegistrationSessionMetadataRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .build());

    assertEquals(GetRegistrationSessionMetadataResponse.ResponseCase.ERROR, response.getResponseCase());
    assertEquals(GetRegistrationSessionMetadataErrorType.GET_REGISTRATION_SESSION_METADATA_ERROR_TYPE_NOT_FOUND,
        response.getError().getErrorType());

    assertFalse(response.hasSessionMetadata());
  }

  @Test
  void sendVerificationCode() {
    final UUID sessionUuid = UUID.randomUUID();

    when(registrationService.sendVerificationCode(any(), any(), isNull(), any(), any()))
        .thenReturn(CompletableFuture.completedFuture(RegistrationSession.newBuilder()
            .setId(UUIDUtil.uuidToByteString(sessionUuid))
            .build()));

    final SendVerificationCodeResponse response =
        blockingStub.sendVerificationCode(SendVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(sessionUuid))
            .setTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setAcceptLanguage("en")
            .build());

    verify(registrationService)
        .sendVerificationCode(MessageTransport.SMS, sessionUuid, null, Locale.LanguageRange.parse("en"), ClientType.UNKNOWN);

    assertEquals(sessionUuid, UUIDUtil.uuidFromByteString(response.getSessionMetadata().getSessionId()));
    assertTrue(response.hasSessionMetadata());
    assertFalse(response.hasError());
  }

  @Test
  void sendVerificationCodeSenderRejected() {
    when(registrationService.sendVerificationCode(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new SenderRejectedRequestException("Oh no!")));

    final SendVerificationCodeResponse response =
        blockingStub.sendVerificationCode(SendVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setAcceptLanguage("en")
            .build());

    assertFalse(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SENDER_REJECTED,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void sendVerificationCodeIllegalSenderArgument() {
    when(registrationService.sendVerificationCode(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new IllegalSenderArgumentException(new RuntimeException())));

    final SendVerificationCodeResponse response =
        blockingStub.sendVerificationCode(SendVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setAcceptLanguage("en")
            .build());

    assertFalse(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SENDER_ILLEGAL_ARGUMENT,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void sendVerificationCodeRateLimited() {
    final Duration retryAfter = Duration.ofSeconds(79);

    when(registrationService.sendVerificationCode(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new RateLimitExceededException(retryAfter,
            RegistrationSession.newBuilder().build())));

    final SendVerificationCodeResponse response =
        blockingStub.sendVerificationCode(SendVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setAcceptLanguage("en")
            .build());

    assertTrue(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_RATE_LIMITED,
        response.getError().getErrorType());

    assertTrue(response.getError().getMayRetry());
    assertEquals(retryAfter.getSeconds(), response.getError().getRetryAfterSeconds());
  }

  @Test
  void sendVerificationCodeSessionNotFound() {
    when(registrationService.sendVerificationCode(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new SessionNotFoundException()));

    final SendVerificationCodeResponse response =
        blockingStub.sendVerificationCode(SendVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setAcceptLanguage("en")
            .build());

    assertFalse(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SESSION_NOT_FOUND,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void sendVerificationCodeAlreadyVerified() {
    when(registrationService.sendVerificationCode(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.failedFuture(
            new SessionAlreadyVerifiedException(RegistrationSession.newBuilder().build())));

    final SendVerificationCodeResponse response =
        blockingStub.sendVerificationCode(SendVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setTransport(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS)
            .setAcceptLanguage("en")
            .build());

    assertTrue(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SESSION_ALREADY_VERIFIED,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void checkVerificationCode() {
    final UUID sessionId = UUID.randomUUID();
    final String verificationCode = "123456";

    when(registrationService.checkVerificationCode(sessionId, verificationCode))
        .thenReturn(CompletableFuture.completedFuture(RegistrationSession.newBuilder()
            .setId(UUIDUtil.uuidToByteString(sessionId))
            .setVerifiedCode(verificationCode)
            .build()));

    final CheckVerificationCodeResponse response =
        blockingStub.checkVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(sessionId))
            .setVerificationCode(verificationCode)
            .build());

    verify(registrationService).checkVerificationCode(sessionId, verificationCode);

    assertTrue(response.hasSessionMetadata());
    assertFalse(response.hasError());
    assertTrue(response.getSessionMetadata().getVerified());
    assertEquals(UUIDUtil.uuidToByteString(sessionId), response.getSessionMetadata().getSessionId());
  }

  @Test
  void checkVerificationCodeNoCodeSent() {
    when(registrationService.checkVerificationCode(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(
            new NoVerificationCodeSentException(RegistrationSession.newBuilder().build())));

    final CheckVerificationCodeResponse response =
        blockingStub.checkVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setVerificationCode("123456")
            .build());

    assertTrue(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_NO_CODE_SENT,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void checkVerificationCodeRateLimited() {
    final Duration retryAfter = Duration.ofSeconds(29);

    when(registrationService.checkVerificationCode(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(
            new RateLimitExceededException(retryAfter, RegistrationSession.newBuilder().build())));

    final CheckVerificationCodeResponse response =
        blockingStub.checkVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setVerificationCode("123456")
            .build());

    assertTrue(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_RATE_LIMITED,
        response.getError().getErrorType());

    assertTrue(response.getError().getMayRetry());
    assertEquals(retryAfter.getSeconds(), response.getError().getRetryAfterSeconds());
  }

  @Test
  void checkVerificationCodeAttemptsExhausted() {
    when(registrationService.checkVerificationCode(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(
            new RateLimitExceededException(null, RegistrationSession.newBuilder().build())));

    final CheckVerificationCodeResponse response =
        blockingStub.checkVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setVerificationCode("123456")
            .build());

    assertTrue(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_RATE_LIMITED,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void checkVerificationCodeSessionNotFound() {
    when(registrationService.checkVerificationCode(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new SessionNotFoundException()));

    final CheckVerificationCodeResponse response =
        blockingStub.checkVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setVerificationCode("123456")
            .build());

    assertFalse(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_SESSION_NOT_FOUND,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @Test
  void checkVerificationCodeAttemptExpired() {
    when(registrationService.checkVerificationCode(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(new AttemptExpiredException()));

    final CheckVerificationCodeResponse response =
        blockingStub.checkVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
            .setVerificationCode("123456")
            .build());

    assertFalse(response.hasSessionMetadata());
    assertTrue(response.hasError());
    assertEquals(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_ATTEMPT_EXPIRED,
        response.getError().getErrorType());

    assertFalse(response.getError().getMayRetry());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void legacyCheckVerificationCode(final boolean verified) {
    final UUID sessionId = UUID.randomUUID();
    final String verificationCode = "123456";

    when(registrationService.legacyCheckVerificationCode(sessionId, verificationCode))
        .thenReturn(CompletableFuture.completedFuture(verified));

    final LegacyCheckVerificationCodeResponse response =
        blockingStub.legacyCheckVerificationCode(CheckVerificationCodeRequest.newBuilder()
            .setSessionId(UUIDUtil.uuidToByteString(sessionId))
            .setVerificationCode(verificationCode)
            .build());

    verify(registrationService).legacyCheckVerificationCode(sessionId, verificationCode);

    assertEquals(verified, response.getVerified());
    assertFalse(response.hasError());
  }

  @ParameterizedTest
  @MethodSource
  void getServiceClientType(final org.signal.registration.rpc.ClientType rpcClientType, final ClientType expectedServiceClientType) {
    assertEquals(expectedServiceClientType, RegistrationServiceGrpcEndpoint.getServiceClientType(rpcClientType));
  }

  private static Stream<Arguments> getServiceClientType() {
    return Stream.of(
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_IOS, ClientType.IOS),
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITH_FCM, ClientType.ANDROID_WITH_FCM),
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITHOUT_FCM, ClientType.ANDROID_WITHOUT_FCM),
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_UNSPECIFIED, ClientType.UNKNOWN),
        Arguments.of(org.signal.registration.rpc.ClientType.UNRECOGNIZED, ClientType.UNKNOWN));
  }
}
