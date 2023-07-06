/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import threegpp.charset.gsm.GSMCharset;
import threegpp.charset.ucs2.UCS2Charset80;

class VerificationSmsBodyLengthTest {

  private static final int SMS_SEGMENT_LENGTH = 160;

  private static final CharsetEncoder GSM7_ENCODER = new GSMCharset().newEncoder();
  private static final CharsetEncoder UCS2_ENCODER = new UCS2Charset80().newEncoder();

  @ParameterizedTest
  @MethodSource
  void messageFitsInSingleSMSSegment(final File propertiesFile, final String key) throws IOException {
    final Properties properties = new Properties();

    try (final FileReader fileReader = new FileReader(propertiesFile)) {
      properties.load(fileReader);
    }

    final String message = properties.getProperty(key)
        .replace("{code}", "123456")
        .replace("{appHash}", "12345678901");

    assertTrue(getEncodedMessageLength(message) <= SMS_SEGMENT_LENGTH);
  }

  private static int getEncodedMessageLength(final String message) throws CharacterCodingException {
    try {
      return GSM7_ENCODER.encode(CharBuffer.wrap(message)).remaining();
    } catch (final CharacterCodingException e) {
      return UCS2_ENCODER.encode(CharBuffer.wrap(message)).remaining();
    }
  }

  private static Stream<Arguments> messageFitsInSingleSMSSegment() throws URISyntaxException {
    final URL resourceUrl = Objects.requireNonNull(VerificationSmsBodyLengthTest.class.getResource("sms.properties"));
    final File resourceDirectory = Objects.requireNonNull(Paths.get(resourceUrl.toURI()).getParent().toFile());

    //noinspection ConstantConditions
    return Arrays.stream(resourceDirectory.listFiles(file ->
            file.getName().startsWith("sms") && file.getName().endsWith(".properties")))
        .flatMap(file -> {
          final Properties properties = new Properties();

          try (final FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          }

          return properties.keySet().stream().map(key -> Arguments.of(file, key));
        });
  }
}
