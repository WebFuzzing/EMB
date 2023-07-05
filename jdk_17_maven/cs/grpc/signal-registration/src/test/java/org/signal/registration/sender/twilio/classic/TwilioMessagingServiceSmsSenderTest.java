/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.classic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.twilio.http.TwilioRestClient;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.UnsupportedMessageTransportException;
import org.signal.registration.sender.VerificationCodeGenerator;
import org.signal.registration.sender.VerificationSmsBodyProvider;

class TwilioMessagingServiceSmsSenderTest {

  @Test
  void sendVerificationCodeUnsupportedTransport() {
    final TwilioMessagingServiceSmsSender sender = new TwilioMessagingServiceSmsSender(mock(TwilioRestClient.class),
        mock(VerificationCodeGenerator.class),
        mock(VerificationSmsBodyProvider.class),
        new TwilioMessagingConfiguration(),
        mock(ApiClientInstrumenter.class));

    assertThrows(UnsupportedMessageTransportException.class, () -> sender.sendVerificationCode(MessageTransport.VOICE,
        PhoneNumberUtil.getInstance().getExampleNumber("US"),
        Collections.emptyList(),
        ClientType.UNKNOWN).join());
  }
}
