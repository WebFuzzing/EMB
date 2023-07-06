/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micronaut.context.MessageSource;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VerificationSmsBodyProviderTest {

  private VerificationSmsConfiguration configuration;

  private static final Phonenumber.PhoneNumber US_NUMBER = PhoneNumberUtil.getInstance().getExampleNumber("US");
  private static final Phonenumber.PhoneNumber CN_NUMBER = PhoneNumberUtil.getInstance().getExampleNumber("CN");

  @BeforeEach
  void setUp() {
    configuration = new VerificationSmsConfiguration();
    configuration.setAndroidAppHash("app-hash");
    configuration.setSupportedLanguages(List.of("en"));
  }

  @ParameterizedTest
  @MethodSource
  void getMessageBody(final Phonenumber.PhoneNumber phoneNumber, final ClientType clientType, final List<Locale.LanguageRange> languageRanges) {
    final VerificationSmsBodyProvider bodyProvider = new VerificationSmsBodyProvider(configuration, new SimpleMeterRegistry());
    final String verificationCode = new VerificationCodeGenerator().generateVerificationCode();

    final String messageBody =
        assertDoesNotThrow(() -> bodyProvider.getVerificationBody(phoneNumber, clientType, verificationCode, languageRanges));

    assertTrue(messageBody.contains(verificationCode));
  }

  private static Stream<Arguments> getMessageBody() {
    return Stream.of(
        Arguments.of(US_NUMBER, ClientType.IOS, Locale.LanguageRange.parse("en")),
        Arguments.of(US_NUMBER, ClientType.ANDROID_WITHOUT_FCM, Locale.LanguageRange.parse("en")),
        Arguments.of(US_NUMBER, ClientType.ANDROID_WITH_FCM, Locale.LanguageRange.parse("en")),
        Arguments.of(US_NUMBER, ClientType.UNKNOWN, Locale.LanguageRange.parse("en")),
        Arguments.of(US_NUMBER, ClientType.UNKNOWN, Collections.emptyList()),
        Arguments.of(CN_NUMBER, ClientType.IOS, Locale.LanguageRange.parse("zh")),
        Arguments.of(CN_NUMBER, ClientType.ANDROID_WITHOUT_FCM, Locale.LanguageRange.parse("zh")),
        Arguments.of(CN_NUMBER, ClientType.ANDROID_WITH_FCM, Locale.LanguageRange.parse("zh")),
        Arguments.of(CN_NUMBER, ClientType.UNKNOWN, Locale.LanguageRange.parse("zh")),
        Arguments.of(CN_NUMBER, ClientType.UNKNOWN, Collections.emptyList())
    );
  }

  @Test
  void getMessageBodyChina() {
    final VerificationSmsBodyProvider bodyProvider = new VerificationSmsBodyProvider(configuration, new SimpleMeterRegistry());

    assertFalse(bodyProvider.getVerificationBody(US_NUMBER, ClientType.UNKNOWN, "123456", Locale.LanguageRange.parse("fr")).contains("\u2008"));
    assertTrue(bodyProvider.getVerificationBody(CN_NUMBER, ClientType.UNKNOWN, "123456", Locale.LanguageRange.parse("fr")).contains("\u2008"));
  }

  @Test
  void getMessageBodyWithVariant() {
    final String recognizedVariant = "fancy";
    final String unrecognizedVariant = "unrecognized";

    final String boringVerificationMessage = "This SMS is boring.";
    final String fancyVerificationMessage = "This SMS is fancy.";

    configuration.setMessageVariantsByRegion(Map.of(
        "mx", recognizedVariant,
        "fr", unrecognizedVariant));

    final MessageSource messageSource = mock(MessageSource.class);

    when(messageSource.getRequiredMessage(
        eq(VerificationSmsBodyProvider.GENERIC_MESSAGE_KEY),
        any(MessageSource.MessageContext.class)))
        .thenReturn(boringVerificationMessage);

    when(messageSource.getMessage(
        eq(VerificationSmsBodyProvider.getMessageKeyForVariant(VerificationSmsBodyProvider.GENERIC_MESSAGE_KEY, recognizedVariant)),
        any(MessageSource.MessageContext.class)))
        .thenReturn(Optional.of(fancyVerificationMessage));

    final VerificationSmsBodyProvider bodyProvider = new VerificationSmsBodyProvider(configuration, messageSource, new SimpleMeterRegistry());

    final Phonenumber.PhoneNumber phoneNumberWithoutVariant = PhoneNumberUtil.getInstance().getExampleNumber("US");
    final Phonenumber.PhoneNumber phoneNumberWithVariant = PhoneNumberUtil.getInstance().getExampleNumber("MX");
    final Phonenumber.PhoneNumber phoneNumberWithUnrecognizedVariant = PhoneNumberUtil.getInstance().getExampleNumber("FR");

    assertEquals(boringVerificationMessage,
        bodyProvider.getVerificationBody(phoneNumberWithoutVariant, ClientType.UNKNOWN, "123456", Locale.LanguageRange.parse("en")));

    assertEquals(fancyVerificationMessage,
        bodyProvider.getVerificationBody(phoneNumberWithVariant, ClientType.UNKNOWN, "123456", Locale.LanguageRange.parse("en")));

    assertEquals(boringVerificationMessage,
        bodyProvider.getVerificationBody(phoneNumberWithUnrecognizedVariant, ClientType.UNKNOWN, "123456", Locale.LanguageRange.parse("en")));
  }

  @Test
  void supportsLanguage() {
    final VerificationSmsConfiguration configuration = new VerificationSmsConfiguration();
    configuration.setAndroidAppHash("app-hash");
    configuration.setSupportedLanguages(List.of("fr"));

    final VerificationSmsBodyProvider bodyProvider =
        new VerificationSmsBodyProvider(configuration, new SimpleMeterRegistry());

    assertFalse(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("en")));
    assertTrue(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("fr")));
    assertTrue(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("fr-CA")));
    assertTrue(bodyProvider.supportsLanguage(Locale.LanguageRange.parse("fr-FR")));
  }
}
