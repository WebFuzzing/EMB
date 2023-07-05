/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.convert;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import jakarta.inject.Singleton;
import java.util.Optional;

/**
 * A type converter that parses strings as phone numbers.
 */
@Singleton
class PhoneNumberFromStringTypeConverter implements TypeConverter<String, Phonenumber.PhoneNumber> {

  @Override
  public Optional<Phonenumber.PhoneNumber> convert(final String string,
      final Class<Phonenumber.PhoneNumber> targetType,
      final ConversionContext context) {

    final String e164 = string.startsWith("+") ? string : "+" + string;

    try {
      return Optional.of(PhoneNumberUtil.getInstance().parse(e164, null));
    } catch (final NumberParseException e) {
      return Optional.empty();
    }
  }
}
