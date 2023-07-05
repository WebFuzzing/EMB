/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

/**
 * Indicates that an error occurred when making a request to an external verification code sender.
 */
public abstract class SenderException extends Exception {

  public SenderException() {
    super();
  }

  public SenderException(final String message) {
    super(message);
  }

  public SenderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public SenderException(final Throwable cause) {
    super(cause);
  }
}
