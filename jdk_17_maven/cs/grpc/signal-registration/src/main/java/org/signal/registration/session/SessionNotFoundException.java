/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session;

/**
 * Indicates that a {@link SessionRepository} could not locate a stored session with a given identifier.
 */
public class SessionNotFoundException extends Exception {
}
