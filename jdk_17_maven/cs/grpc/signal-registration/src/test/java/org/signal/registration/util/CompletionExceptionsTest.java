/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.twilio.exception.ApiException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

class CompletionExceptionsTest {

  @Test
  void unwrap() {
    final ApiException apiException = new ApiException("test");
    final CompletionException completionExceptionWithoutCause = new CompletionException(null);

    assertTrue(CompletionExceptions.unwrap(apiException) instanceof ApiException);
    assertTrue(CompletionExceptions.unwrap(new CompletionException(apiException)) instanceof ApiException);
    assertTrue(CompletionExceptions.unwrap(new UncheckedIOException(new IOException())) instanceof UncheckedIOException);
    assertEquals(completionExceptionWithoutCause, CompletionExceptions.unwrap(completionExceptionWithoutCause));
    assertNull(CompletionExceptions.unwrap(null));
  }

  @Test
  void rethrow() {
    final CompletionException completionException = new CompletionException("Already a completion exception", null);
    final IllegalStateException illegalStateException = new IllegalStateException("Not a completion exception");

    assertEquals(completionException, CompletionExceptions.wrap(completionException));
    assertEquals(illegalStateException, CompletionExceptions.wrap(illegalStateException).getCause());
    assertEquals(CompletionExceptions.unwrap(null), null);
  }
}
