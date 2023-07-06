/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.prescribed;

import com.google.common.annotations.VisibleForTesting;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.SenderRejectedRequestException;
import org.signal.registration.sender.VerificationCodeSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A verification code sender that always returns a pre-configured verification code for a set of configured, generally
 * fictitious, phone numbers. This strategy is intended only to support testing.
 */
@Singleton
@Requires(bean = PrescribedVerificationCodeRepository.class)
public class PrescribedVerificationCodeSender implements VerificationCodeSender {

  private final PrescribedVerificationCodeRepository verificationCodeRepository;

  private final AtomicReference<Map<Phonenumber.PhoneNumber, String>> prescribedVerificationCodes =
      new AtomicReference<>(Collections.emptyMap());

  private static final Logger logger = LoggerFactory.getLogger(PrescribedVerificationCodeSender.class);

  public PrescribedVerificationCodeSender(final PrescribedVerificationCodeRepository verificationCodeRepository) {
    this.verificationCodeRepository = verificationCodeRepository;
  }

  @Scheduled(fixedRate = "10s")
  @VisibleForTesting
  public CompletableFuture<Void> refreshPhoneNumbers() {
    return verificationCodeRepository.getVerificationCodes().thenAccept(prescribedVerificationCodes::set);
  }

  @Override
  public String getName() {
    return "prescribed-verification-code";
  }

  @Override
  public Duration getAttemptTtl() {
    return Duration.ofMinutes(10);
  }

  @Override
  public boolean supportsDestination(final MessageTransport messageTransport,
      final Phonenumber.PhoneNumber phoneNumber,
      final List<Locale.LanguageRange> languageRanges,
      final ClientType clientType) {

    return prescribedVerificationCodes.get().containsKey(phoneNumber);
  }

  @Override
  public CompletableFuture<AttemptData> sendVerificationCode(final MessageTransport messageTransport,
                                                             final Phonenumber.PhoneNumber phoneNumber,
                                                             final List<Locale.LanguageRange> languageRanges,
                                                             final ClientType clientType) {

    final String verificationCode = prescribedVerificationCodes.get().get(phoneNumber);

    return StringUtils.isNotBlank(verificationCode) ?
        CompletableFuture.completedFuture(new AttemptData(Optional.empty(),
            PrescribedVerificationCodeSessionData.newBuilder()
                .setVerificationCode(verificationCode)
                .build()
                .toByteArray())) :
        CompletableFuture.failedFuture(new SenderRejectedRequestException("Unsupported phone number"));
  }

  @Override
  public CompletableFuture<Boolean> checkVerificationCode(final String verificationCode, final byte[] senderData) {
    try {
      final String expectedVerificationCode =
          PrescribedVerificationCodeSessionData.parseFrom(senderData).getVerificationCode();

      return CompletableFuture.completedFuture(StringUtils.equals(verificationCode, expectedVerificationCode));
    } catch (final InvalidProtocolBufferException e) {
      logger.error("Failed to parse stored session data", e);
      return CompletableFuture.failedFuture(e);
    }
  }
}
