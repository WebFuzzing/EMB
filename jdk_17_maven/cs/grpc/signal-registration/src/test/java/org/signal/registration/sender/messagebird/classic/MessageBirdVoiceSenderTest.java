/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.signal.registration.sender.messagebird.classic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.messagebird.MessageBirdClient;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.MessageResponse;
import com.messagebird.objects.VoiceMessage;
import com.messagebird.objects.VoiceMessageResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.VerificationCodeGenerator;
import org.signal.registration.sender.VerificationTTSBodyProvider;
import org.signal.registration.sender.messagebird.SenderIdSelector;
import org.signal.registration.util.CompletionExceptions;

public class MessageBirdVoiceSenderTest {

  private static final Phonenumber.PhoneNumber NUMBER = PhoneNumberUtil.getInstance().getExampleNumber("US");
  private static final String E164 = PhoneNumberUtil.getInstance()
      .format(NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164);
  private static final List<Locale.LanguageRange> EN = Locale.LanguageRange.parse("en");
  private VerificationCodeGenerator codeGenerator;
  private VerificationTTSBodyProvider bodyProvider;
  private MessageBirdClient client;
  private MessageBirdVoiceSender sender;


  @BeforeEach
  public void setup() {
    final MessageBirdVoiceConfiguration config = new MessageBirdVoiceConfiguration(
        3,
        Duration.ofSeconds(1),
        List.of("en-gb"));
    codeGenerator = mock(VerificationCodeGenerator.class);
    client = mock(MessageBirdClient.class);
    bodyProvider = mock(VerificationTTSBodyProvider.class);

    final SenderIdSelector senderId = mock(SenderIdSelector.class);

    when(senderId.getSenderId(NUMBER)).thenReturn("test");

    sender = new MessageBirdVoiceSender(Runnable::run, config, codeGenerator, bodyProvider, client,
        mock(ApiClientInstrumenter.class),
        senderId);
  }

  private static VoiceMessageResponse response(int failedDeliveryCount) {
    MessageResponse.Recipients recipients = mock(MessageResponse.Recipients.class);
    when(recipients.getTotalDeliveryFailedCount()).thenReturn(failedDeliveryCount);
    final VoiceMessageResponse response = mock(VoiceMessageResponse.class);
    when(response.getRecipients()).thenReturn(recipients);
    when(response.getId()).thenReturn("test");
    return response;
  }

  @Test
  public void errorSend() throws GeneralException, UnauthorizedException {
    when(client.sendVoiceMessage(argThat((VoiceMessage message) ->
        message.getRecipients().equals(E164))))
        .thenThrow(new GeneralException("test"));
    when(codeGenerator.generateVerificationCode()).thenReturn("123456");
    when(bodyProvider.supportsLanguage(any())).thenReturn(true);
    when(bodyProvider.getVerificationBody(any(), any(), any(), any())).thenReturn("body");
    final Throwable error = CompletionExceptions.unwrap(assertThrows(CompletionException.class, () -> sender
        .sendVerificationCode(MessageTransport.VOICE, NUMBER, EN, ClientType.IOS)
        .join()));
    assertTrue(error instanceof GeneralException);
  }

  @Test
  public void sendAndVerify() throws GeneralException, UnauthorizedException {
    when(codeGenerator.generateVerificationCode()).thenReturn("123456");
    when(bodyProvider.getVerificationBody(NUMBER, ClientType.IOS, "123456", EN))
        .thenReturn("body");

    final VoiceMessageResponse response = response(0);
    when(client.sendVoiceMessage(argThat((VoiceMessage message) ->
        message.getRecipients().equals(E164)
            && message.getBody().contains("body")
            && message.getLanguage().equals("en-gb"))))
        .thenReturn(response);

    AttemptData result = sender
        .sendVerificationCode(MessageTransport.VOICE, NUMBER, EN, ClientType.IOS)
        .join();
    assertFalse(sender.checkVerificationCode("1234567", result.senderData()).join());
    assertTrue(sender.checkVerificationCode("123456", result.senderData()).join());
  }

}
