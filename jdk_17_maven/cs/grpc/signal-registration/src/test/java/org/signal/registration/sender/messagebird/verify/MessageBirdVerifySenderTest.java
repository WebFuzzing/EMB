/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.messagebird.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.messagebird.MessageBirdClient;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.Language;
import com.messagebird.objects.Verify;
import com.messagebird.objects.VerifyRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.sender.ApiClientInstrumenter;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.sender.VerificationSmsBodyProvider;
import org.signal.registration.sender.messagebird.SenderIdSelector;
import javax.annotation.Nullable;

public class MessageBirdVerifySenderTest {

  private static final Phonenumber.PhoneNumber NUMBER = PhoneNumberUtil.getInstance().getExampleNumber("US");
  private static final String E164 = PhoneNumberUtil.getInstance()
      .format(NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164);


  private MessageBirdClient client;
  private MessageBirdVerifySender sender;
  private VerificationSmsBodyProvider bodyProvider;

  @BeforeEach
  public void setup() {
    final MessageBirdVerifyConfiguration config = new MessageBirdVerifyConfiguration(Duration.ofSeconds(1));
    client = mock(MessageBirdClient.class);
    bodyProvider = mock(VerificationSmsBodyProvider.class);
    when(bodyProvider.getVerificationBody(any(), any(), any(), any())).thenReturn("test sms");
    sender = new MessageBirdVerifySender(config, Runnable::run, client, bodyProvider,
        mock(ApiClientInstrumenter.class), mock(SenderIdSelector.class));
  }

  public static Stream<Arguments> langSupport() {
    return Stream.of(
        // all mb supported languages should be supported
        Arrays.stream(Language.values())
            .map(Language::getCode)
            .map(lang -> Arguments.of(lang, true)),

        // same tags without extlang should also be supported
        Arrays.stream(Language.values())
            .map(Language::getCode)
            .map(l -> l.split("-")[0])
            .map(lang -> Arguments.of(lang, true)),

        Stream.of(
            Arguments.of("en", true),
            Arguments.of("bloop", false),
            Arguments.of("en,en-US", true)
        )
    ).flatMap(Function.identity());
  }

  @ParameterizedTest
  @MethodSource
  public void langSupport(String langRange, boolean supported) {
    assertEquals(
        supported,
        sender.supportsDestination(MessageTransport.VOICE, NUMBER, Locale.LanguageRange.parse(langRange),
            ClientType.IOS));
  }

  public static Stream<Arguments> langMapping() {
    return Stream.of(
        Arguments.of("en-US", Language.EN_US),
        Arguments.of("en-US,en", Language.EN_US),
        Arguments.of("en", Language.EN_GB),
        Arguments.of("de", Language.DE_DE),
        Arguments.of("fjkd", null),
        Arguments.of("en-au", Language.EN_AU),
        Arguments.of("en-gb", Language.EN_GB)
    );
  }

  @ParameterizedTest
  @MethodSource
  public void langMapping(final String langRange, final @Nullable Language expected) {
    assertEquals(
        expected,
        sender.lookupMessageBirdLanguage(Locale.LanguageRange.parse(langRange)).orElse(null));
  }


  @Test
  public void success() throws GeneralException, UnauthorizedException, NotFoundException {
    Verify createResponse = new Verify();
    createResponse.setStatus("sent");
    createResponse.setId("myid");

    when(client.sendVerifyToken(argThat((VerifyRequest req) -> E164.equals(req.getRecipient()))))
        .thenReturn(createResponse);

    final byte[] senderData = sender.sendVerificationCode(
        MessageTransport.SMS,
        NUMBER,
        Collections.emptyList(),
        ClientType.IOS).join()
        .senderData();

    Verify verifyResponse = new Verify();
    verifyResponse.setStatus("verified");

    when(client.verifyToken("myid", "12345")).thenReturn(verifyResponse);
    assertTrue(sender.checkVerificationCode("12345", senderData).join());
  }

  enum Outcome {CREATE_ERROR, VERIFY_ERROR, NOT_VERIFIED}

  static Stream<Arguments> failureStatus() {
    return Stream.of(
        Arguments.of("failed", "", Outcome.CREATE_ERROR),
        Arguments.of("expired", "", Outcome.CREATE_ERROR),
        Arguments.of("sent", "sent", Outcome.VERIFY_ERROR),
        Arguments.of("sent", "expired", Outcome.NOT_VERIFIED),
        Arguments.of("sent", "deleted", Outcome.NOT_VERIFIED),
        Arguments.of("sent", "failed", Outcome.NOT_VERIFIED));
  }

  @ParameterizedTest
  @MethodSource
  public void failureStatus(String createStatus, String verifyStatus, Outcome outcome)
      throws GeneralException, UnauthorizedException, NotFoundException {
    Verify createResponse = new Verify();
    createResponse.setStatus(createStatus);
    createResponse.setId("myid");

    when(client.sendVerifyToken(argThat((VerifyRequest req) -> E164.equals(req.getRecipient()))))
        .thenReturn(createResponse);

    final CompletableFuture<AttemptData> sendFuture = sender.sendVerificationCode(
        MessageTransport.SMS,
        NUMBER,
        Collections.emptyList(),
        ClientType.IOS);

    if (outcome == Outcome.CREATE_ERROR) {
      assertThrows(CompletionException.class, sendFuture::join);
      return;
    }

    byte[] senderData = sendFuture.join().senderData();
    Verify verifyResponse = new Verify();
    verifyResponse.setStatus(verifyStatus);

    when(client.verifyToken("myid", "12345")).thenReturn(verifyResponse);
    final CompletableFuture<Boolean> verifyFut = sender.checkVerificationCode("12345", senderData);
    if (outcome == Outcome.VERIFY_ERROR) {
      assertThrows(CompletionException.class, verifyFut::join);
    } else {
      assertFalse(verifyFut.join());
    }
  }

}
