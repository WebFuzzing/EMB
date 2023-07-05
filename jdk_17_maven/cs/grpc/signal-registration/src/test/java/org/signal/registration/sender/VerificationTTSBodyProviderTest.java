/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VerificationTTSBodyProviderTest {

  private VerificationVoiceConfiguration configuration;

  private static final Phonenumber.PhoneNumber NUMBER = PhoneNumberUtil.getInstance().getExampleNumber("US");

  @BeforeEach
  void setUp() {
    configuration = new VerificationVoiceConfiguration(List.of("en"));
  }

  @Test
  void getMessageBody() {
    final VerificationTTSBodyProvider bodyProvider = new VerificationTTSBodyProvider(configuration,
        new SimpleMeterRegistry());
    final String verificationCode = "123456";

    final String messageBody =
        assertDoesNotThrow(() -> bodyProvider.getVerificationBody(NUMBER, ClientType.IOS, verificationCode,
            Locale.LanguageRange.parse("en")));

    assertTrue(messageBody.contains("1, 2, 3, 4, 5, 6"));

    final String untranslatedMessageBody =
        assertDoesNotThrow(() -> bodyProvider.getVerificationBody(NUMBER, ClientType.IOS, verificationCode,
            Locale.LanguageRange.parse("fr")));

    assertTrue(untranslatedMessageBody.contains("1, 2, 3, 4, 5, 6"));
  }

  @Test
  void getTranslationVariants() {
    final VerificationTTSBodyProvider bodyProvider = new VerificationTTSBodyProvider(
        new VerificationVoiceConfiguration(List.of("pt", "pt-BR", "pt-PT")),
        new SimpleMeterRegistry());
    final String verificationCode = "123456";

    final String portugueseBr =
        assertDoesNotThrow(() -> bodyProvider.getVerificationBody(NUMBER, ClientType.IOS, verificationCode,
            Locale.LanguageRange.parse("pt-BR")));

    final String portuguesePt =
        assertDoesNotThrow(() -> bodyProvider.getVerificationBody(NUMBER, ClientType.IOS, verificationCode,
            Locale.LanguageRange.parse("pt-PT")));

    final String portugueseDefault =
        assertDoesNotThrow(() -> bodyProvider.getVerificationBody(NUMBER, ClientType.IOS, verificationCode,
            Locale.LanguageRange.parse("pt")));

    assertEquals(portuguesePt, portugueseDefault);
    assertNotEquals(portugueseBr, portuguesePt);

  }

  @Test
  void supportsLanguage() {
    final VerificationVoiceConfiguration configuration = new VerificationVoiceConfiguration(List.of("fr"));
    final VerificationTTSBodyProvider bodyProvider = new VerificationTTSBodyProvider(
        configuration, new SimpleMeterRegistry());

    assertFalse(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("en")));
    assertTrue(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("fr")));
    assertTrue(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("fr-CA")));
    assertTrue(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("fr-FR")));
  }
}
