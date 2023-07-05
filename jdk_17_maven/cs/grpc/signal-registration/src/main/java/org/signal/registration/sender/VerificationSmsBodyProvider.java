/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import com.google.common.annotations.VisibleForTesting;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.context.MessageSource;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.signal.registration.util.PhoneNumbers;

/**
 * A verification SMS body provider supplies localized, client-appropriate body text for use in SMS verification
 * messages.
 * <p>
 * Message text is defined in the {@code sms.properties} resource file, and additional translations may be provided by
 * adding language-specific properties files as explained in
 * <a href="https://docs.micronaut.io/latest/guide/#i18n">Micronaut's internationalization documentation</a>. Each
 * localization should contain at least the following keys:
 *
 * <ul>
 *   <li>{@code verification.sms.ios}</li>
 *   <li>{@code verification.sms.android}</li>
 *   <li>{@code verification.sms.generic}</li>
 * </ul>
 * <p>
 * Message text may have "variants." Variants may be configured by destination phone number region and should have
 * corresponding entries in message properties files. For example, if messages to US-based phone numbers should receive
 * messages with a "verbose" variant, then a mapping from {@code "US"} to {@code "verbose"} should be added to the map
 * returned by {@link VerificationSmsConfiguration#getMessageVariantsByRegion()} and the following properties should be
 * added to the various message string tables:
 *
 * <ul>
 *   <li>{@code verification.sms.ios.verbose}</li>
 *   <li>{@code verification.sms.android.verbose}</li>
 *   <li>{@code verification.sms.generic.verbose}</li>
 * </ul>
 *
 * @see <a href="https://docs.micronaut.io/latest/guide/#i18n">Micronaut - Internationalization</a>
 * @see VerificationSmsConfiguration#getMessageVariantsByRegion()
 */
@Singleton
public class VerificationSmsBodyProvider extends AbstractVerificationBodyProvider {

  private final VerificationSmsConfiguration configuration;

  private final MessageSource messageSource;

  @VisibleForTesting
  static final String IOS_MESSAGE_KEY = "verification.sms.ios";

  @VisibleForTesting
  static final String ANDROID_MESSAGE_KEY = "verification.sms.android";

  @VisibleForTesting
  static final String GENERIC_MESSAGE_KEY = "verification.sms.generic";

  private static final int COUNTRY_CODE_CN = 86;

  @Inject
  public VerificationSmsBodyProvider(final VerificationSmsConfiguration configuration, final MeterRegistry meterRegistry) {
    this(configuration, new ResourceBundleMessageSource("org.signal.registration.sender.sms"), meterRegistry);
  }

  @VisibleForTesting
  VerificationSmsBodyProvider(final VerificationSmsConfiguration configuration, final MessageSource messageSource, final MeterRegistry meterRegistry) {
    super(configuration.getSupportedLanguages(), meterRegistry);
    this.configuration = configuration;
    this.messageSource = messageSource;
  }

  @VisibleForTesting
  static String getMessageKeyForVariant(final String baseMessageKey, final String variant) {
    return baseMessageKey + "." + variant;
  }

  @Override
  protected String lookupBody(@Nullable final Locale locale, final Phonenumber.PhoneNumber phoneNumber,
      final ClientType clientType, final String verificationCode) {
    final String messageKey = switch (clientType) {
      case IOS -> IOS_MESSAGE_KEY;
      case ANDROID_WITH_FCM -> ANDROID_MESSAGE_KEY;
      default -> GENERIC_MESSAGE_KEY;
    };

    final String regionCode = PhoneNumbers.regionCodeUpper(phoneNumber);

    final Optional<String> maybeMessageKeyWithVariant =
        Optional.ofNullable(configuration.getMessageVariantsByRegion().get(regionCode))
            .map(variant -> getMessageKeyForVariant(messageKey, variant));
    final MessageSource.MessageContext messageContext =
        MessageSource.MessageContext.of(locale, Map.of(
            "code", verificationCode,
            "appHash", configuration.getAndroidAppHash()));

    final String message = maybeMessageKeyWithVariant
        .flatMap(keyWithVariant -> messageSource.getMessage(keyWithVariant, messageContext))
        .orElseGet(() -> messageSource.getRequiredMessage(messageKey, messageContext));

    // Twilio recommends adding this character to the end of strings delivered to China because some carriers in China
    // are blocking GSM-7 encoding and this will force Twilio to send using UCS-2 instead.
    return phoneNumber.getCountryCode() == COUNTRY_CODE_CN ? message + "\u2008" : message;
  }

  @Override
  protected MessageTransport transport() {
    return MessageTransport.SMS;
  }
}
