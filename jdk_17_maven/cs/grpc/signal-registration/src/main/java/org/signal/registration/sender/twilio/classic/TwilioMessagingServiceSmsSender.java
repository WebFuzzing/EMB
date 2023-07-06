/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.classic;

import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.UnsupportedMessageTransportException;
import org.signal.registration.sender.VerificationCodeGenerator;
import org.signal.registration.sender.VerificationCodeSender;
import org.signal.registration.sender.VerificationSmsBodyProvider;
import org.signal.registration.sender.twilio.ApiExceptions;
import org.signal.registration.util.CompletionExceptions;

/**
 * A concrete implementation of an {@code AbstractTwilioProvidedCodeSender} that sends its codes via the Twilio
 * Programmable Messaging API.
 */
@Singleton
public class TwilioMessagingServiceSmsSender extends AbstractTwilioProvidedCodeSender implements
    VerificationCodeSender {

  private final TwilioRestClient twilioRestClient;
  private final VerificationCodeGenerator verificationCodeGenerator;
  private final VerificationSmsBodyProvider verificationSmsBodyProvider;
  private final TwilioMessagingConfiguration configuration;
  private final ApiClientInstrumenter apiClientInstrumenter;

  public static final String SENDER_NAME = "twilio-programmable-messaging";

  public TwilioMessagingServiceSmsSender(final TwilioRestClient twilioRestClient,
      final VerificationCodeGenerator verificationCodeGenerator,
      final VerificationSmsBodyProvider verificationSmsBodyProvider,
      final TwilioMessagingConfiguration configuration,
      final ApiClientInstrumenter apiClientInstrumenter) {
    this.twilioRestClient = twilioRestClient;
    this.verificationCodeGenerator = verificationCodeGenerator;
    this.verificationSmsBodyProvider = verificationSmsBodyProvider;
    this.configuration = configuration;
    this.apiClientInstrumenter = apiClientInstrumenter;
  }

  @Override
  public String getName() {
    return SENDER_NAME;
  }

  @Override
  public Duration getAttemptTtl() {
    return configuration.getSessionTtl();
  }

  @Override
  public boolean supportsDestination(final MessageTransport messageTransport,
      final Phonenumber.PhoneNumber phoneNumber,
      final List<Locale.LanguageRange> languageRanges,
      final ClientType clientType) {

    return messageTransport == MessageTransport.SMS && verificationSmsBodyProvider.supportsLanguage(languageRanges);
  }

  @Override
  public CompletableFuture<AttemptData> sendVerificationCode(final MessageTransport messageTransport,
                                                             final Phonenumber.PhoneNumber phoneNumber,
                                                             final List<Locale.LanguageRange> languageRanges,
                                                             final ClientType clientType) throws UnsupportedMessageTransportException {

    if (messageTransport != MessageTransport.SMS) {
      throw new UnsupportedMessageTransportException();
    }

    final String messagingServiceSid = phoneNumber.getCountryCode() == 1 ?
        configuration.getNanpaMessagingServiceSid() : configuration.getGlobalMessagingServiceSid();

    final String verificationCode = verificationCodeGenerator.generateVerificationCode();

    final Timer.Sample sample = Timer.start();

    return Message.creator(twilioNumberFromPhoneNumber(phoneNumber), messagingServiceSid,
            verificationSmsBodyProvider.getVerificationBody(phoneNumber, clientType, verificationCode, languageRanges))
        .createAsync(twilioRestClient)
        .whenComplete((message, throwable) ->
            apiClientInstrumenter.recordApiCallMetrics(
                this.getName(),
                "message.create",
                throwable == null,
                ApiExceptions.extractErrorCode(throwable),
                sample))
        .handle((message, throwable) -> {
          if (throwable == null) {
            return buildAttemptMetadata(message.getSid(), verificationCode);
          }

          throw CompletionExceptions.wrap(ApiExceptions.toSenderException(throwable));
        });
  }
}
