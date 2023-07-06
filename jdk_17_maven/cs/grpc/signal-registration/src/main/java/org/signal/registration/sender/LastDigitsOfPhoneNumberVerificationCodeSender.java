/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import org.signal.registration.Environments;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A trivial verification code "sender" that never actually sends codes, but instead always uses the last six digits of
 * the destination phone number as a verification code. This sender is intended only for local testing and should never
 * be used in a production environment.
 */
@Singleton
@Requires(env = {Environments.DEVELOPMENT, Environment.TEST})
public class LastDigitsOfPhoneNumberVerificationCodeSender implements VerificationCodeSender {

  @Override
  public String getName() {
    return "last-digits-of-phone-number";
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

    return true;
  }

  @Override
  public CompletableFuture<AttemptData> sendVerificationCode(final MessageTransport messageTransport,
                                                             final Phonenumber.PhoneNumber phoneNumber,
                                                             final List<Locale.LanguageRange> languageRanges,
                                                             final ClientType clientType) {

        return CompletableFuture.completedFuture(
            new AttemptData(Optional.empty(), getVerificationCode(phoneNumber).getBytes(StandardCharsets.UTF_8)));
  }

  @Override
  public CompletableFuture<Boolean> checkVerificationCode(final String verificationCode, final byte[] senderData) {
    return CompletableFuture.completedFuture(verificationCode.equals(new String(senderData, StandardCharsets.UTF_8)));
  }

  public static String getVerificationCode(final Phonenumber.PhoneNumber phoneNumber) {
    final String e164String = PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    return e164String.substring(e164String.length() - 6);
  }
}
