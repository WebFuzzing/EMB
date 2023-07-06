/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.verify;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.InvalidProtocolBufferException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.rest.verify.v2.service.VerificationCreator;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.UnsupportedMessageTransportException;
import org.signal.registration.sender.VerificationCodeSender;
import org.signal.registration.sender.twilio.ApiExceptions;
import org.signal.registration.util.CompletionExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Twilio Verify sender sends verification codes to end users via Twilio Verify.
 */
@Singleton
public class TwilioVerifySender implements VerificationCodeSender {

  private final TwilioRestClient twilioRestClient;
  private final TwilioVerifyConfiguration configuration;
  private final ApiClientInstrumenter apiClientInstrumenter;

  public static final String SENDER_NAME = "twilio-verify";

  private static final Map<MessageTransport, Verification.Channel> CHANNELS_BY_TRANSPORT = new EnumMap<>(Map.of(
      MessageTransport.SMS, Verification.Channel.SMS,
      MessageTransport.VOICE, Verification.Channel.CALL
  ));

  private static final Logger logger = LoggerFactory.getLogger(TwilioVerifySender.class);

  protected TwilioVerifySender(
      final TwilioRestClient twilioRestClient,
      final TwilioVerifyConfiguration configuration,
      final ApiClientInstrumenter apiClientInstrumenter) {
    this.twilioRestClient = twilioRestClient;
    this.configuration = configuration;
    this.apiClientInstrumenter = apiClientInstrumenter;
  }

  @Override
  public boolean supportsDestination(final MessageTransport messageTransport,
      final Phonenumber.PhoneNumber phoneNumber,
      final List<Locale.LanguageRange> languageRanges,
      final ClientType clientType) {
    return CHANNELS_BY_TRANSPORT.containsKey(messageTransport) &&
        Locale.lookupTag(languageRanges, configuration.getSupportedLanguages()) != null;
  }

  @Override
  public String getName() {
    return SENDER_NAME;
  }

  @Override
  public Duration getAttemptTtl() {
    // Upstream sessions time out after ten minutes; see
    // https://support.twilio.com/hc/en-us/articles/360033354913-What-is-the-Default-Verify-V2-Expiration-Time-
    return Duration.ofMinutes(10);
  }

  @Override
  public CompletableFuture<AttemptData> sendVerificationCode(final MessageTransport messageTransport,
                                                             final Phonenumber.PhoneNumber phoneNumber,
                                                             final List<Locale.LanguageRange> languageRanges,
                                                             final ClientType clientType) throws UnsupportedMessageTransportException {

    final Verification.Channel channel = CHANNELS_BY_TRANSPORT.get(messageTransport);

    if (channel == null) {
      throw new UnsupportedMessageTransportException();
    }

    final VerificationCreator verificationCreator =
        Verification.creator(configuration.getServiceSid(),
                PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164),
                channel.toString())
            .setCustomFriendlyName(configuration.getServiceFriendlyName())
            .setLocale(Locale.lookupTag(languageRanges, configuration.getSupportedLanguages()));

    if (clientType == ClientType.ANDROID_WITH_FCM) {
      verificationCreator.setAppHash(configuration.getAndroidAppHash());
    }

    final Timer.Sample sample = Timer.start();

    return verificationCreator.createAsync(twilioRestClient)
        .whenComplete((sessionData, throwable) ->
            this.apiClientInstrumenter.recordApiCallMetrics(
                this.getName(),
                "verification." + messageTransport.name().toLowerCase() + ".create",
                throwable == null,
                ApiExceptions.extractErrorCode(throwable),
                sample)
        )
        .handle((verification, throwable) -> {
          if (throwable == null) {
            final Optional<String> maybeAttemptSid = verification.getSendCodeAttempts().stream()
                .filter(attempt -> attempt.containsKey("attempt_sid"))
                .findFirst()
                .map(attempt -> attempt.get("attempt_sid").toString());

            return new AttemptData(maybeAttemptSid, TwilioVerifySessionData.newBuilder()
                .setVerificationSid(verification.getSid())
                .build()
                .toByteArray());
          }

          throw CompletionExceptions.wrap(ApiExceptions.toSenderException(throwable));
        });
  }

  @Override
  public CompletableFuture<Boolean> checkVerificationCode(final String verificationCode, final byte[] senderData) {
    try {
      final String verificationSid = TwilioVerifySessionData.parseFrom(senderData).getVerificationSid();

      final Timer.Sample sample = Timer.start();

      return VerificationCheck.creator(configuration.getServiceSid())
          .setVerificationSid(verificationSid)
          .setCode(verificationCode)
          .createAsync(twilioRestClient)
          .thenApply(VerificationCheck::getValid)
          .handle((verificationCheck, throwable) -> {
            if (throwable == null) {
              return verificationCheck;
            }

            throw CompletionExceptions.wrap(ApiExceptions.toSenderException(throwable));
          })
          .whenComplete((verificationCheck, throwable) ->
              apiClientInstrumenter.recordApiCallMetrics(
                  this.getName(),
                  "verification_check.create",
                  throwable == null,
                  ApiExceptions.extractErrorCode(throwable),
                  sample));
    } catch (final InvalidProtocolBufferException e) {
      logger.error("Failed to parse stored session data", e);
      return CompletableFuture.failedFuture(e);
    }
  }
}
