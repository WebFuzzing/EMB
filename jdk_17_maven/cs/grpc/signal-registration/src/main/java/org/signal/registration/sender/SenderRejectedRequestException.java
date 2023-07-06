/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

/**
 * Indicates that a request to a verification code sender was well-formed, but the sender declined to process the
 * request (e.g. due to exhausted attempts or suspected fraud). Rejected request exceptions are generally permanent and
 * should not be retried by callers.
 */
public class SenderRejectedRequestException extends SenderException {

  public SenderRejectedRequestException(final String message) {
    super(message);
  }

  public SenderRejectedRequestException(final Throwable cause) {
    super(cause);
  }
}
