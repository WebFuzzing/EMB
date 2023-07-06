/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session;

/**
 * A "session completed" event is triggered when a session's TTL has expired and no more changes to that session are
 * possible.
 *
 * @param session the registration session that has been completed
 */
public record SessionCompletedEvent(RegistrationSession session) {
}
