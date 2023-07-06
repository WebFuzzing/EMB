/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.classic;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.InvalidProtocolBufferException;
import com.twilio.type.PhoneNumber;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.VerificationCodeSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base class for verification code senders that use Twilio services, but generate their own verification
 * codes (i.e. Twilio services other than Verify).
 */
abstract class AbstractTwilioProvidedCodeSender implements VerificationCodeSender {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  protected static AttemptData buildAttemptMetadata(final String sid, final String verificationCode) {
    return new AttemptData(Optional.of(sid), TwilioProvidedCodeSessionData.newBuilder()
        .setVerificationCode(verificationCode)
        .build()
        .toByteArray());
  }

  protected static PhoneNumber twilioNumberFromPhoneNumber(final Phonenumber.PhoneNumber phoneNumber) {
    return new PhoneNumber(PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
  }

  @Override
  public CompletableFuture<Boolean> checkVerificationCode(final String verificationCode, final byte[] senderData) {
    try {
      return CompletableFuture.completedFuture(StringUtils.equals(verificationCode,
              TwilioProvidedCodeSessionData.parseFrom(senderData).getVerificationCode()));
    } catch (final InvalidProtocolBufferException e) {
      logger.error("Failed to parse stored session data", e);
      return CompletableFuture.failedFuture(e);
    }
  }
}
