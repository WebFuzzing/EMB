/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.rpc;

import com.google.common.annotations.VisibleForTesting;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.AttemptExpiredException;
import org.signal.registration.NoVerificationCodeSentException;
import org.signal.registration.RegistrationService;
import org.signal.registration.SessionAlreadyVerifiedException;
import org.signal.registration.ratelimit.RateLimitExceededException;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.IllegalSenderArgumentException;
import org.signal.registration.sender.SenderRejectedRequestException;
import org.signal.registration.session.SessionMetadata;
import org.signal.registration.session.SessionNotFoundException;
import org.signal.registration.util.CompletionExceptions;
import org.signal.registration.util.MessageTransports;
import org.signal.registration.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RegistrationServiceGrpcEndpoint extends RegistrationServiceGrpc.RegistrationServiceImplBase {

  final RegistrationService registrationService;

  private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceGrpcEndpoint.class);

  public RegistrationServiceGrpcEndpoint(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @Override
  public void createSession(final CreateRegistrationSessionRequest request,
      final StreamObserver<CreateRegistrationSessionResponse> responseObserver) {

    try {
      final Phonenumber.PhoneNumber phoneNumber =
          PhoneNumberUtil.getInstance().parse("+" + request.getE164(), null);

      registrationService.createRegistrationSession(phoneNumber, SessionMetadata.newBuilder()
              .setAccountExistsWithE164(request.getAccountExistsWithE164())
              .build())
          .whenComplete((session, throwable) -> {
            if (throwable == null) {
              responseObserver.onNext(CreateRegistrationSessionResponse.newBuilder()
                  .setSessionMetadata(registrationService.buildSessionMetadata(session))
                  .build());

              responseObserver.onCompleted();
            } else {
              buildCreateSessionErrorResponse(CompletionExceptions.unwrap(throwable)).ifPresentOrElse(errorResponse -> {
                responseObserver.onNext(errorResponse);
                responseObserver.onCompleted();
              }, () -> {
                logger.warn("Failed to create registration session", throwable);
                responseObserver.onError(new StatusException(Status.INTERNAL));
              });
            }
          });
    } catch (final NumberParseException e) {
      responseObserver.onNext(CreateRegistrationSessionResponse.newBuilder()
          .setError(CreateRegistrationSessionError.newBuilder()
              .setErrorType(CreateRegistrationSessionErrorType.CREATE_REGISTRATION_SESSION_ERROR_TYPE_ILLEGAL_PHONE_NUMBER)
              .setMayRetry(false)
              .build())
          .build());

      responseObserver.onCompleted();
    }
  }

  private Optional<CreateRegistrationSessionResponse> buildCreateSessionErrorResponse(final Throwable cause) {
    if (cause instanceof RateLimitExceededException rateLimitExceededException) {
      final CreateRegistrationSessionError.Builder errorBuilder = CreateRegistrationSessionError.newBuilder()
          .setErrorType(CreateRegistrationSessionErrorType.CREATE_REGISTRATION_SESSION_ERROR_TYPE_RATE_LIMITED)
          .setMayRetry(rateLimitExceededException.getRetryAfterDuration().isPresent());

      rateLimitExceededException.getRetryAfterDuration()
          .ifPresent(retryAfterDuration -> errorBuilder.setRetryAfterSeconds(retryAfterDuration.getSeconds()));

      return Optional.of(CreateRegistrationSessionResponse.newBuilder()
          .setError(errorBuilder.build())
          .build());
    }

    return Optional.empty();
  }

  @Override
  public void getSessionMetadata(final GetRegistrationSessionMetadataRequest request,
      final StreamObserver<GetRegistrationSessionMetadataResponse> responseObserver) {

    try {
      registrationService.getRegistrationSession(UUIDUtil.uuidFromByteString(request.getSessionId()))
          .whenComplete((session, throwable) -> {
            if (throwable == null) {
              responseObserver.onNext(GetRegistrationSessionMetadataResponse.newBuilder()
                  .setSessionMetadata(registrationService.buildSessionMetadata(session))
                  .build());

              responseObserver.onCompleted();
            } else {
              buildGetSessionMetadataErrorResponse(CompletionExceptions.unwrap(throwable)).ifPresentOrElse(errorResponse -> {
                responseObserver.onNext(errorResponse);
                responseObserver.onCompleted();
              }, () -> {
                logger.warn("Failed to get session metadata", throwable);
                responseObserver.onError(new StatusException(Status.INTERNAL));
              });
            }
          });
    } catch (final IllegalArgumentException e) {
      responseObserver.onError(new StatusException(Status.INVALID_ARGUMENT));
    }
  }

  private Optional<GetRegistrationSessionMetadataResponse> buildGetSessionMetadataErrorResponse(final Throwable cause) {
    if (cause instanceof SessionNotFoundException) {
      return Optional.of(GetRegistrationSessionMetadataResponse.newBuilder()
          .setError(GetRegistrationSessionMetadataError.newBuilder()
              .setErrorType(GetRegistrationSessionMetadataErrorType.GET_REGISTRATION_SESSION_METADATA_ERROR_TYPE_NOT_FOUND)
              .build())
          .build());
    }

    return Optional.empty();
  }

  @Override
  public void sendVerificationCode(final SendVerificationCodeRequest request,
      final StreamObserver<SendVerificationCodeResponse> responseObserver) {

    try {
      registrationService.sendVerificationCode(MessageTransports.getSenderMessageTransportFromRpcTransport(request.getTransport()),
              UUIDUtil.uuidFromByteString(request.getSessionId()),
              request.getSenderName().isBlank() ? null : request.getSenderName(),
              getLanguageRanges(request.getAcceptLanguage()),
              getServiceClientType(request.getClientType()))
          .whenComplete((session, throwable) -> {
            if (throwable == null) {
              responseObserver.onNext(SendVerificationCodeResponse.newBuilder()
                  .setSessionMetadata(registrationService.buildSessionMetadata(session))
                  .build());

              responseObserver.onCompleted();
            } else {
              buildSendVerificationCodeErrorResponse(CompletionExceptions.unwrap(throwable)).ifPresentOrElse(errorResponse -> {
                responseObserver.onNext(errorResponse);
                responseObserver.onCompleted();
              }, () -> {
                logger.warn("Failed to send registration code", throwable);
                responseObserver.onError(new StatusException(Status.INTERNAL));
              });
            }
          });
    } catch (final IllegalArgumentException e) {
      responseObserver.onError(new StatusException(Status.INVALID_ARGUMENT));
    }
  }

  private Optional<SendVerificationCodeResponse> buildSendVerificationCodeErrorResponse(final Throwable cause) {
    if (cause instanceof SessionAlreadyVerifiedException sessionAlreadyVerifiedException) {
      return Optional.of(SendVerificationCodeResponse.newBuilder()
          .setSessionMetadata(registrationService.buildSessionMetadata(sessionAlreadyVerifiedException.getRegistrationSession()))
          .setError(SendVerificationCodeError.newBuilder()
              .setErrorType(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SESSION_ALREADY_VERIFIED)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof SessionNotFoundException) {
      return Optional.of(SendVerificationCodeResponse.newBuilder()
          .setError(SendVerificationCodeError.newBuilder()
              .setErrorType(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SESSION_NOT_FOUND)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof RateLimitExceededException rateLimitExceededException) {
      final SendVerificationCodeError.Builder errorBuilder = SendVerificationCodeError.newBuilder()
          .setErrorType(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_RATE_LIMITED)
          .setMayRetry(rateLimitExceededException.getRetryAfterDuration().isPresent());

      rateLimitExceededException.getRetryAfterDuration()
          .ifPresent(retryAfterDuration -> errorBuilder.setRetryAfterSeconds(retryAfterDuration.getSeconds()));

      final SendVerificationCodeResponse.Builder responseBuilder = SendVerificationCodeResponse.newBuilder()
          .setError(errorBuilder.build())
          .setSessionMetadata(registrationService.buildSessionMetadata(
              rateLimitExceededException.getRegistrationSession().orElseThrow(() ->
                  new IllegalStateException("Rate limit exception did not include a session reference"))));

      return Optional.of(responseBuilder.build());
    } else if (cause instanceof SenderRejectedRequestException) {
      return Optional.of(SendVerificationCodeResponse.newBuilder()
          .setError(SendVerificationCodeError.newBuilder()
              .setErrorType(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SENDER_REJECTED)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof IllegalSenderArgumentException) {
      return Optional.of(SendVerificationCodeResponse.newBuilder()
          .setError(SendVerificationCodeError.newBuilder()
              .setErrorType(SendVerificationCodeErrorType.SEND_VERIFICATION_CODE_ERROR_TYPE_SENDER_ILLEGAL_ARGUMENT)
              .setMayRetry(false)
              .build())
          .build());
    }

    return Optional.empty();
  }

  @Override
  public void checkVerificationCode(final CheckVerificationCodeRequest request,
      final StreamObserver<CheckVerificationCodeResponse> responseObserver) {

    registrationService.checkVerificationCode(UUIDUtil.uuidFromByteString(request.getSessionId()), request.getVerificationCode())
        .whenComplete((session, throwable) -> {
          if (throwable == null) {
            responseObserver.onNext(CheckVerificationCodeResponse.newBuilder()
                .setSessionMetadata(registrationService.buildSessionMetadata(session))
                .build());

            responseObserver.onCompleted();
          } else {
            buildCheckVerificationCodeErrorResponse(CompletionExceptions.unwrap(throwable)).ifPresentOrElse(errorResponse -> {
              responseObserver.onNext(errorResponse);
              responseObserver.onCompleted();
            }, () -> {
              logger.warn("Failed to check verification code", throwable);
              responseObserver.onError(new StatusException(Status.INTERNAL));
            });
          }
        });
  }

  private Optional<CheckVerificationCodeResponse> buildCheckVerificationCodeErrorResponse(final Throwable cause) {
    if (cause instanceof NoVerificationCodeSentException noVerificationCodeSentException) {
      return Optional.of(CheckVerificationCodeResponse.newBuilder()
          .setSessionMetadata(registrationService.buildSessionMetadata(noVerificationCodeSentException.getRegistrationSession()))
          .setError(CheckVerificationCodeError.newBuilder()
              .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_NO_CODE_SENT)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof SessionNotFoundException) {
      return Optional.of(CheckVerificationCodeResponse.newBuilder()
          .setError(CheckVerificationCodeError.newBuilder()
              .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_SESSION_NOT_FOUND)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof RateLimitExceededException rateLimitExceededException) {
      final CheckVerificationCodeError.Builder errorBuilder = CheckVerificationCodeError.newBuilder()
          .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_RATE_LIMITED)
          .setMayRetry(rateLimitExceededException.getRetryAfterDuration().isPresent());

      rateLimitExceededException.getRetryAfterDuration()
          .ifPresent(retryAfterDuration -> errorBuilder.setRetryAfterSeconds(retryAfterDuration.getSeconds()));

      final CheckVerificationCodeResponse.Builder responseBuilder = CheckVerificationCodeResponse.newBuilder()
          .setError(errorBuilder.build())
          .setSessionMetadata(registrationService.buildSessionMetadata(
              rateLimitExceededException.getRegistrationSession().orElseThrow(() ->
                  new IllegalStateException("Rate limit exception did not include a session reference"))));

      return Optional.of(responseBuilder.build());
    } else if (cause instanceof AttemptExpiredException) {
      return Optional.of(CheckVerificationCodeResponse.newBuilder()
          .setError(CheckVerificationCodeError.newBuilder()
              .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_ATTEMPT_EXPIRED)
              .setMayRetry(false)
              .build())
          .build());
    }

    return Optional.empty();
  }

  @Deprecated
  @Override
  public void legacyCheckVerificationCode(final CheckVerificationCodeRequest request,
      final StreamObserver<LegacyCheckVerificationCodeResponse> responseObserver) {

    registrationService.legacyCheckVerificationCode(UUIDUtil.uuidFromByteString(request.getSessionId()), request.getVerificationCode())
        .whenComplete((verified, throwable) -> {
          if (throwable == null) {
            responseObserver.onNext(LegacyCheckVerificationCodeResponse.newBuilder()
                .setVerified(verified)
                .build());

            responseObserver.onCompleted();
          } else {
            buildLegacyCheckVerificationCodeErrorResponse(CompletionExceptions.unwrap(throwable)).ifPresentOrElse(errorResponse -> {
              responseObserver.onNext(errorResponse);
              responseObserver.onCompleted();
            }, () -> {
              logger.warn("Failed to check verification code", throwable);
              responseObserver.onError(new StatusException(Status.INTERNAL));
            });
          }
        });
  }

  @Deprecated
  private Optional<LegacyCheckVerificationCodeResponse> buildLegacyCheckVerificationCodeErrorResponse(final Throwable cause) {
    if (cause instanceof NoVerificationCodeSentException) {
      return Optional.of(LegacyCheckVerificationCodeResponse.newBuilder()
          .setError(CheckVerificationCodeError.newBuilder()
              .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_NO_CODE_SENT)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof SessionNotFoundException) {
      return Optional.of(LegacyCheckVerificationCodeResponse.newBuilder()
          .setError(CheckVerificationCodeError.newBuilder()
              .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_SESSION_NOT_FOUND)
              .setMayRetry(false)
              .build())
          .build());
    } else if (cause instanceof RateLimitExceededException rateLimitExceededException) {
      final CheckVerificationCodeError.Builder errorBuilder = CheckVerificationCodeError.newBuilder()
          .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_RATE_LIMITED)
          .setMayRetry(rateLimitExceededException.getRetryAfterDuration().isPresent());

      rateLimitExceededException.getRetryAfterDuration()
          .ifPresent(retryAfterDuration -> errorBuilder.setRetryAfterSeconds(retryAfterDuration.getSeconds()));

      final LegacyCheckVerificationCodeResponse.Builder responseBuilder = LegacyCheckVerificationCodeResponse.newBuilder()
          .setError(errorBuilder.build());

      return Optional.of(responseBuilder.build());
    } else if (cause instanceof AttemptExpiredException) {
      return Optional.of(LegacyCheckVerificationCodeResponse.newBuilder()
          .setError(CheckVerificationCodeError.newBuilder()
              .setErrorType(CheckVerificationCodeErrorType.CHECK_VERIFICATION_CODE_ERROR_TYPE_ATTEMPT_EXPIRED)
              .setMayRetry(false)
              .build())
          .build());
    }

    return Optional.empty();
  }

  @VisibleForTesting
  static List<Locale.LanguageRange> getLanguageRanges(final String acceptLanguageList) {
    if (StringUtils.isBlank(acceptLanguageList)) {
      return Collections.emptyList();
    }

    try {
      return Locale.LanguageRange.parse(acceptLanguageList);
    } catch (final IllegalArgumentException e) {
      logger.debug("Could not get acceptable languages from language list; \"{}\"", acceptLanguageList, e);
      return Collections.emptyList();
    }
  }

  @VisibleForTesting
  static ClientType getServiceClientType(final org.signal.registration.rpc.ClientType rpcClientType) {
    return switch (rpcClientType) {
      case CLIENT_TYPE_IOS -> ClientType.IOS;
      case CLIENT_TYPE_ANDROID_WITH_FCM -> ClientType.ANDROID_WITH_FCM;
      case CLIENT_TYPE_ANDROID_WITHOUT_FCM -> ClientType.ANDROID_WITHOUT_FCM;
      case CLIENT_TYPE_UNSPECIFIED, UNRECOGNIZED -> ClientType.UNKNOWN;
    };
  }
}
