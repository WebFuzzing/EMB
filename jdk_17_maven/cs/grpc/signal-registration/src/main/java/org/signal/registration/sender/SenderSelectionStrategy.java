/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import com.google.i18n.phonenumbers.Phonenumber;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A sender selection strategy chooses a {@link VerificationCodeSender} for the given message transport, destination
 * phone number, language preferences, and client type.
 */
public interface SenderSelectionStrategy {

  /**
   * Selects a verification code sender for the given message transport, destination phone number, language preferences,
   * and client type.
   *
   * @param transport       the message transport via which to send a verification code
   * @param phoneNumber     the phone number to which to send a verification code
   * @param languageRanges  a prioritized list of language preferences for the receiver of the verification code
   * @param clientType      the type of client receiving the verification code
   * @param preferredSender if provided, a sender to use
   * @return a verification code sender appropriate for the given message transport, phone number, language preferences,
   * and client type
   */
  VerificationCodeSender chooseVerificationCodeSender(MessageTransport transport,
      Phonenumber.PhoneNumber phoneNumber,
      List<Locale.LanguageRange> languageRanges,
      ClientType clientType,
      @Nullable String preferredSender);
}
