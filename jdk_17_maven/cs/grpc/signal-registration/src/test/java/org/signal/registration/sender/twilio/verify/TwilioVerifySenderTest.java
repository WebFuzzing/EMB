/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.http.TwilioRestClient;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;

class TwilioVerifySenderTest {

  private TwilioVerifySender twilioVerifySender;

  @BeforeEach
  void setUp() {
    final TwilioVerifyConfiguration configuration = new TwilioVerifyConfiguration();
    configuration.setAndroidAppHash("app-hash");
    configuration.setServiceSid("service-sid");
    configuration.setServiceFriendlyName("friendly-name");
    configuration.setSupportedLanguages(List.of("en"));
    twilioVerifySender = new TwilioVerifySender(mock(TwilioRestClient.class), configuration,
        mock(ApiClientInstrumenter.class));
  }

  @ParameterizedTest
  @MethodSource
  void supportsDestination(final MessageTransport messageTransport,
      final Phonenumber.PhoneNumber phoneNumber,
      final List<Locale.LanguageRange> languageRanges,
      final ClientType clientType,
      final boolean expectSupportsDestination) {

    assertEquals(expectSupportsDestination,
        twilioVerifySender.supportsDestination(messageTransport, phoneNumber, languageRanges, clientType));
  }

  private static Stream<Arguments> supportsDestination() throws NumberParseException {
    final Phonenumber.PhoneNumber phoneNumber =
        PhoneNumberUtil.getInstance().parse("+12025550123", null);

    return Stream.of(
        Arguments.of(MessageTransport.SMS, phoneNumber, Locale.LanguageRange.parse("en"), ClientType.IOS, true),
        Arguments.of(MessageTransport.VOICE, phoneNumber, Locale.LanguageRange.parse("en"), ClientType.IOS, true),
        Arguments.of(MessageTransport.SMS, phoneNumber, Locale.LanguageRange.parse("en"),
            ClientType.ANDROID_WITHOUT_FCM, true),
        Arguments.of(MessageTransport.SMS, phoneNumber, Locale.LanguageRange.parse("en"), ClientType.ANDROID_WITH_FCM,
            true),
        Arguments.of(MessageTransport.SMS, phoneNumber, Locale.LanguageRange.parse("en"), ClientType.UNKNOWN, true),
        Arguments.of(MessageTransport.SMS, phoneNumber, Locale.LanguageRange.parse("ja"), ClientType.IOS, false),
        Arguments.of(MessageTransport.VOICE, phoneNumber, Locale.LanguageRange.parse("ja"), ClientType.IOS, false),
        Arguments.of(MessageTransport.SMS, phoneNumber, Locale.LanguageRange.parse("ja,en;q=0.4"), ClientType.IOS, true),
        Arguments.of(MessageTransport.VOICE, phoneNumber, Locale.LanguageRange.parse("ja,en;q=0.4"), ClientType.IOS, true));
  }
}
