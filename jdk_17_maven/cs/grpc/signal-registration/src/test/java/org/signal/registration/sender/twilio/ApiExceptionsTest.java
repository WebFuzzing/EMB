/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.twilio.exception.ApiException;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;
import org.signal.registration.sender.SenderRejectedRequestException;

class ApiExceptionsTest {

  @Test
  void extractErrorCode() {
    assertEquals("1234", ApiExceptions.extractErrorCode(new ApiException("Test", 1234, null, 4321, null)));

    assertEquals("1234", ApiExceptions.extractErrorCode(new CompletionException(
        new ApiException("Test", 1234, null, 4321, null))));

    assertEquals("1234", ApiExceptions.extractErrorCode(new CompletionException(
        new SenderRejectedRequestException(
            new ApiException("Test", 1234, null, 4321, null)))));
  }
}
