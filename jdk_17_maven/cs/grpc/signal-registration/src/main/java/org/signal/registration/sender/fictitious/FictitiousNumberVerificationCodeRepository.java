/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.fictitious;

import com.google.i18n.phonenumbers.Phonenumber;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * A fictitious number verification code repository stores verification codes for fictitious phone numbers (i.e. numbers
 * that are syntactically valid, but could not receive a text message or phone call) in an external data store. The data
 * store is expected to be readable by external services with the intent of facilitating testing.
 *
 * @see FictitiousNumberVerificationCodeSender
 */
public interface FictitiousNumberVerificationCodeRepository {

  /**
   * Store a verification code for a given phone number in an external data store.
   *
   * @param phoneNumber the phone number for which to store a verification code
   * @param verificationCode the verification code for the given phone number
   * @param ttl the time after which the verification code is no longer valid and may be discarded
   *
   * @return a future that completes when the verification code has been stored
   */
  CompletableFuture<Void> storeVerificationCode(Phonenumber.PhoneNumber phoneNumber,
      String verificationCode,
      final Duration ttl);
}
