/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import com.google.i18n.phonenumbers.Phonenumber;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.context.MessageSource;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A verification voice body provider supplies localized, client-appropriate string for voice verification messages
 * generated via text to speech.
 * <p/>
 * Message text is defined in the {@code voice.properties} resource file, and additional translations may be provided by
 * adding language-specific properties files as explained in
 * <a href="https://docs.micronaut.io/latest/guide/#i18n">Micronaut's internationalization documentation</a>. Each
 * localization should contain the key {@code verification.voice}
 *
 * @see <a href="https://docs.micronaut.io/latest/guide/#i18n">Micronaut - Internationalization</a>
 */
@Singleton
public class VerificationTTSBodyProvider extends AbstractVerificationBodyProvider {

  private final MessageSource messageSource = new ResourceBundleMessageSource("org.signal.registration.sender.voice");

  @Inject
  public VerificationTTSBodyProvider(final VerificationVoiceConfiguration configuration,
      final MeterRegistry meterRegistry) {
    super(configuration.supportedLanguages(), meterRegistry);
  }

  @Override
  protected String lookupBody(@Nullable final Locale locale, final Phonenumber.PhoneNumber phoneNumber,
      final ClientType clientType, final String verificationCode) {

    final Map<String, Object> codeDigits = IntStream.range(0, 6).boxed().collect(Collectors.toMap(
        i -> String.format("code%d", i),
        verificationCode::charAt));

    final MessageSource.MessageContext messageContext = MessageSource.MessageContext.of(locale, codeDigits);
    return messageSource.getRequiredMessage("verification.voice", messageContext);
  }

  @Override
  protected MessageTransport transport() {
    return MessageTransport.VOICE;
  }
}
