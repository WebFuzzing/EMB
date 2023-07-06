/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.messagebird;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MessageBirdVerifyAttemptAnalyzerTest {

  @Test
  void getMessageId() {
    assertEquals("31bce2a1155d1f7c1db9df6b32167259",
        MessageBirdVerifyAttemptAnalyzer.getMessageId("https://rest.messagebird.com/messages/31bce2a1155d1f7c1db9df6b32167259"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Not even a URI",
      "https://rest.messagebird.com",
      "https://rest.messagebird.com/messages/31bce2a1155d1f7c1db9df6b32167259/"
  })
  void getMessageIdIllegalUri(final String messageHref) {
    assertThrows(IllegalArgumentException.class, () -> MessageBirdVerifyAttemptAnalyzer.getMessageId(messageHref));
  }
}
