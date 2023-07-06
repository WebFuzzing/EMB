/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LastDigitsOfPhoneNumberVerificationCodeSenderTest {

  private LastDigitsOfPhoneNumberVerificationCodeSender sender;

  @BeforeEach
  void setUp() {
    sender = new LastDigitsOfPhoneNumberVerificationCodeSender();
  }

  @Test
  void getVerificationCode() throws NumberParseException {
    assertEquals("550123", LastDigitsOfPhoneNumberVerificationCodeSender.getVerificationCode(
            PhoneNumberUtil.getInstance().parse("+12025550123", null)));
  }

  @Test
  void sendVerificationCode() throws NumberParseException {
    assertArrayEquals("550123".getBytes(StandardCharsets.UTF_8),
        sender.sendVerificationCode(MessageTransport.SMS,
            PhoneNumberUtil.getInstance().parse("+12025550123", null),
            Collections.emptyList(),
            null).join().senderData());
  }
}
