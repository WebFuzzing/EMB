/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Attempt data contains all the information needed to identify an upstream verification attempt and to allow local
 * senders to verify user-submitted codes at some time in the future.
 *
 * @param remoteId a unique, sender-specific identifier for this attempt; may be empty if the sender does not
 *                 produce unique identifiers for each attempt
 * @param senderData an opaque array of bytes that encodes any data the sender will later need to verify the code sent
 *                   as part of this verification attempt; may be empty, but must not be {@code null}
 */
public record AttemptData(Optional<String> remoteId, byte[] senderData) {
}
