/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.logstash.logback.marker.Markers;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.metrics.MetricsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractVerificationBodyProvider {

  private static final Logger logger = LoggerFactory.getLogger(AbstractVerificationBodyProvider.class);

  private final List<String> supportedLanguages;
  private final MeterRegistry meterRegistry;

  private static final String GET_VERIFICATION_BODY_COUNTER_NAME =
      MetricsUtil.name(AbstractVerificationBodyProvider.class, "getVerificationBody");

  AbstractVerificationBodyProvider(final List<String> supportedLanguages, MeterRegistry meterRegistry) {
    this.supportedLanguages = supportedLanguages;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Tests whether this provider has a translation for at least one language in the provided list of language ranges.
   *
   * @param languageRanges the preferred languages for the body text
   * @return {@code true} if this provider has a translation for at least one of the given language ranges or
   * {@code false} otherwise
   */
  public boolean supportsLanguage(final List<Locale.LanguageRange> languageRanges) {
    return Locale.lookupTag(languageRanges, supportedLanguages) != null;
  }

  /**
   * Generates a localized, client-appropriate message for use in a verification message to the given destination phone
   * number.
   *
   * @param phoneNumber      the phone number that will receive the verification message
   * @param clientType       the type of client that will receive the verification message
   * @param verificationCode the verification code to include in the verification message
   * @param languageRanges   the preferred languages for the verification message
   * @return a localized, client-appropriate verification message that includes the given verification code
   */
  public String getVerificationBody(final Phonenumber.PhoneNumber phoneNumber,
      final ClientType clientType,
      final String verificationCode,
      final List<Locale.LanguageRange> languageRanges) {
    @Nullable final Locale locale;
    {
      final String preferredLanguage =
          Locale.lookupTag(languageRanges, supportedLanguages);

      if (preferredLanguage == null) {
        logger.debug(Markers.appendEntries(Map.of(
                "countryCode", phoneNumber.getCountryCode(),
                "regionCode",
                StringUtils.defaultIfBlank(PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber), "XX"),
                "client", clientType.name(),
                "transport", transport()
            )),
            "No supported language for ranges: {}", languageRanges);
      }

      meterRegistry.counter(GET_VERIFICATION_BODY_COUNTER_NAME,
              "language", StringUtils.defaultIfBlank(preferredLanguage, "unknown"),
              "transport", transport().toString())
          .increment();

      if (StringUtils.isNotBlank(preferredLanguage)) {
        locale = Locale.forLanguageTag(preferredLanguage);
      } else {
        locale = null;
      }
    }
    return lookupBody(locale, phoneNumber, clientType, verificationCode);
  }

  /**
   * Generate a localized message body containing the provided verification code
   *
   * @param locale           locale that either matches a language in {@code this.supportedLanguages} or null
   * @param phoneNumber      the phone number that will receive the verification message
   * @param clientType       the type of client that will receive the verification message
   * @param verificationCode the verification code to include in the verification message
   * @return a localized message body
   */
  protected abstract String lookupBody(
      final @Nullable Locale locale,
      final Phonenumber.PhoneNumber phoneNumber,
      final ClientType clientType,
      final String verificationCode);

  protected abstract MessageTransport transport();

}
