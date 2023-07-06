/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

/**
 * Indicates that a request to a verification code sender was successfully transmitted, but was rejected because the
 * sender did not understand the request or the request contained illegal arguments.
 */
public class IllegalSenderArgumentException extends SenderException {

  public IllegalSenderArgumentException(final Throwable cause) {
    super(cause);
  }
}
