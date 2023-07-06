/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.fictitious;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.signal.registration.sender.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * A fictitious number verification code sender generates random verification codes for phone numbers known to be
 * reserved for fictitious use and stores the verification codes in an external repository. In general, this code sender
 * is intended to facilitate testing, and it is expected that external systems will have controlled, read-only access to
 * the repository in which verification codes are stored.
 *
 * @see FictitiousNumberVerificationCodeRepository
 */
@Singleton
@Requires(bean = FictitiousNumberVerificationCodeRepository.class)
public class FictitiousNumberVerificationCodeSender implements VerificationCodeSender {

  private final VerificationCodeGenerator verificationCodeGenerator;
  private final FictitiousNumberVerificationCodeRepository repository;

  private static final List<Pattern> FICTITIOUS_NUMBER_PATTERNS = List.of(
      // NANPA fictitious numbers
      Pattern.compile("\\+1[0-9]{3}55501[0-9]{2}"),

      // UK "numbers for drama"
      Pattern.compile("\\+447700900[0-9]{3}"),

      // French numbers "for use in audiovisual productions"
      Pattern.compile("\\+3363998[0-9]{4}")
  );

  private static final Logger logger = LoggerFactory.getLogger(FictitiousNumberVerificationCodeSender.class);

  FictitiousNumberVerificationCodeSender(final VerificationCodeGenerator verificationCodeGenerator,
      final FictitiousNumberVerificationCodeRepository repository) {

    this.verificationCodeGenerator = verificationCodeGenerator;
    this.repository = repository;
  }

  @Override
  public String getName() {
    return "fictitious-number";
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

    final String e164 = PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

    return FICTITIOUS_NUMBER_PATTERNS.stream()
        .anyMatch(pattern -> pattern.matcher(e164).matches());
  }

  @Override
  public CompletableFuture<AttemptData> sendVerificationCode(final MessageTransport messageTransport,
      final Phonenumber.PhoneNumber phoneNumber,
      final List<Locale.LanguageRange> languageRanges,
      final ClientType clientType) throws UnsupportedMessageTransportException {

    final String verificationCode = verificationCodeGenerator.generateVerificationCode();

    return repository.storeVerificationCode(phoneNumber, verificationCode, getAttemptTtl())
        .thenApply(ignored -> new AttemptData(Optional.empty(),
            FictitiousNumberVerificationCodeSessionData.newBuilder()
                .setVerificationCode(verificationCode)
                .build()
                .toByteArray()));
  }

  @Override
  public CompletableFuture<Boolean> checkVerificationCode(final String verificationCode, final byte[] senderData) {
    try {
      final String expectedVerificationCode =
          FictitiousNumberVerificationCodeSessionData.parseFrom(senderData).getVerificationCode();

      return CompletableFuture.completedFuture(expectedVerificationCode.equals(verificationCode));
    } catch (final InvalidProtocolBufferException e) {
      logger.error("Failed to parse stored session data", e);
      return CompletableFuture.failedFuture(e);
    }
  }
}
