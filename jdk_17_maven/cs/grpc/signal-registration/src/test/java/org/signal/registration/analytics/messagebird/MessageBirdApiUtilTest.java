/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.messagebird;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.messagebird.objects.MessageResponse;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.analytics.AttemptAnalysis;
import org.signal.registration.analytics.Money;

class MessageBirdApiUtilTest {

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @ParameterizedTest
  @MethodSource
  void extractAttemptAnalysis(@Nullable MessageResponse.Price price, @Nullable final String mcc, @Nullable final String mnc, final Optional<AttemptAnalysis> expectedAnalysis) {
        final MessageResponse.Items items = mock(MessageResponse.Items.class);
    when(items.getPrice()).thenReturn(price);
    when(items.getMcc()).thenReturn(mcc);
    when(items.getMnc()).thenReturn(mnc);

    final MessageResponse.Recipients recipients = mock(MessageResponse.Recipients.class);
    when(recipients.getItems()).thenReturn(List.of(items));

    assertEquals(expectedAnalysis, MessageBirdApiUtil.extractAttemptAnalysis(recipients));
  }

  private static Stream<Arguments> extractAttemptAnalysis() {
    final BigDecimal amount = new BigDecimal("0.25");
    final String mcc = "012";
    final String mnc = "017";

    final MessageResponse.Price price = mock(MessageResponse.Price.class);
    when(price.getAmountDecimal()).thenReturn(amount);
    when(price.getCurrency()).thenReturn(Currency.getInstance("USD").getCurrencyCode());

    return Stream.of(
        // Populated price, but no MCC/MNC
        Arguments.of(price, null, null,
            Optional.of(new AttemptAnalysis(Optional.of(new Money(amount, Currency.getInstance("USD"))), Optional.empty(), Optional.empty()))),

        // Populated price with MCC/MNC
        Arguments.of(price, String.valueOf(mcc), String.valueOf(mnc),
            Optional.of(new AttemptAnalysis(Optional.of(new Money(amount, Currency.getInstance("USD"))), Optional.of(mcc), Optional.of(mnc)))),

        // MCC/MNC present, but empty price
        Arguments.of(mock(MessageResponse.Price.class), String.valueOf(mcc), String.valueOf(mnc),
            Optional.empty()),

        // MCC/MNC present, but no price at all
        Arguments.of(null, String.valueOf(mcc), String.valueOf(mnc),
            Optional.empty())
    );
  }
}
