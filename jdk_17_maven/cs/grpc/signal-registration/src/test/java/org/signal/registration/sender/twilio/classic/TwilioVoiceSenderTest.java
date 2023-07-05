/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.classic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.http.TwilioRestClient;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.UnsupportedMessageTransportException;
import org.signal.registration.sender.VerificationCodeGenerator;

class TwilioVoiceSenderTest {

  private TwilioVoiceSender sender;

  @BeforeEach
  void setUp() {
    final TwilioVoiceConfiguration configuration = new TwilioVoiceConfiguration();
    configuration.setPhoneNumbers(List.of("+12025550123"));
    configuration.setCdnUri(URI.create("https://example.com/verification/"));
    configuration.setSupportedLanguages(List.of("en", "de"));

    sender = new TwilioVoiceSender(
        mock(TwilioRestClient.class),
        new VerificationCodeGenerator(),
        configuration,
        mock(ApiClientInstrumenter.class));
  }

  @ParameterizedTest
  @MethodSource
  void supportsDestination(final List<Locale.LanguageRange> languageRanges, final boolean expectSupported) {
    final Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().getExampleNumber("US");
    assertEquals(expectSupported, sender.supportsDestination(MessageTransport.VOICE, phoneNumber, languageRanges, ClientType.UNKNOWN));
  }

  private static Stream<Arguments> supportsDestination() {
    return Stream.of(
        Arguments.of(Locale.LanguageRange.parse("en"), true),
        Arguments.of(Locale.LanguageRange.parse("de"), true),
        Arguments.of(Locale.LanguageRange.parse("fr"), false),
        Arguments.of(Collections.emptyList(), false));
  }

  @Test
  void sendVerificationCodeUnsupportedTransport() {
    assertThrows(UnsupportedMessageTransportException.class, () -> sender.sendVerificationCode(MessageTransport.SMS,
        PhoneNumberUtil.getInstance().getExampleNumber("US"),
        Collections.emptyList(),
        ClientType.UNKNOWN).join());
  }

  @Test
  void buildCallTwiml() {
    final String twiml = sender.buildCallTwiml("123456", "es").toString();

    assertTrue(twiml.contains("https://example.com/verification/es/verification.mp3"));
    assertTrue(twiml.contains("https://example.com/verification/es/1_middle.mp3"));
    assertTrue(twiml.contains("https://example.com/verification/es/2_middle.mp3"));
    assertTrue(twiml.contains("https://example.com/verification/es/3_middle.mp3"));
    assertTrue(twiml.contains("https://example.com/verification/es/4_middle.mp3"));
    assertTrue(twiml.contains("https://example.com/verification/es/5_middle.mp3"));
    assertTrue(twiml.contains("https://example.com/verification/es/6_falling.mp3"));
  }
}
