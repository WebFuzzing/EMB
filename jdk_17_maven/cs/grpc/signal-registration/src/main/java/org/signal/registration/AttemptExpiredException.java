/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration;

/**
 * Indicates that an attempt to check a verification code failed because the most recent registration attempt in the
 * session has expired.
 */
public class AttemptExpiredException extends NoStackTraceException {
}
