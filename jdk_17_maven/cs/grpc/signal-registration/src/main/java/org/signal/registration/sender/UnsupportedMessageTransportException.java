/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

/**
 * An unsupported message transport exception indicates that a {@link VerificationCodeSender} was asked to send a
 * verification code via a {@link MessageTransport} that it does not support.
 */
public class UnsupportedMessageTransportException extends IllegalArgumentException {
}
