/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.convert;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberFromStringTypeConverterTest {

  private PhoneNumberFromStringTypeConverter typeConverter;

  @BeforeEach
  void setUp() {
    typeConverter = new PhoneNumberFromStringTypeConverter();
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @ParameterizedTest
  @MethodSource
  void convert(final String string, final Optional<Phonenumber.PhoneNumber> expectedPhoneNumber) {
    assertEquals(expectedPhoneNumber, typeConverter.convert(string, Phonenumber.PhoneNumber.class));
  }

  private static Stream<Arguments> convert() throws NumberParseException {
    return Stream.of(
        Arguments.of("+12025550123",
            Optional.of(PhoneNumberUtil.getInstance().parse("+12025550123", null))),

        Arguments.of("12025550123",
            Optional.of(PhoneNumberUtil.getInstance().parse("+12025550123", null))),

        Arguments.of("This is not a valid phone number",
            Optional.empty()));
  }
}
