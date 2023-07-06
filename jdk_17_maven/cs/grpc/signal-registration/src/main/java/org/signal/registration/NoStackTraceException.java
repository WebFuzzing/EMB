/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration;

/**
 * An abstract base class for checked exceptions that do not produce stack traces.
 */
public abstract class NoStackTraceException extends Exception {

  public NoStackTraceException() {
    this(null, null, true);
  }

  public NoStackTraceException(final String message) {
    this(message, null, true);
  }

  public NoStackTraceException(final String message, final Throwable cause) {
    this(message, cause, true);
  }

  public NoStackTraceException(final Throwable cause) {
    this(null, cause, true);
  }

  protected NoStackTraceException(final String message, final Throwable cause, final boolean enableSuppression) {

    super(message, cause, enableSuppression, false);
  }
}
