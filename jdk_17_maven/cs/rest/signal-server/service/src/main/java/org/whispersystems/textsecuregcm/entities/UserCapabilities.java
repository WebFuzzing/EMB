/*
 * Copyright 2013-2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.Account;

public record UserCapabilities(
    @Deprecated(forRemoval = true)
    @JsonProperty("gv1-migration")
    boolean gv1Migration,

    @Deprecated(forRemoval = true)
    boolean senderKey,

    @Deprecated(forRemoval = true)
    boolean announcementGroup,

    @Deprecated(forRemoval = true)
    boolean changeNumber,

    @Deprecated(forRemoval = true)
    boolean stories,

    @Deprecated(forRemoval = true)
    boolean giftBadges,
    boolean paymentActivation,
    boolean pni) {

  public static UserCapabilities createForAccount(Account account) {
    return new UserCapabilities(
        true,
        true,
        true,
        true,
        true,
        true,

        // Hardcode payment activation flag to false until all clients support the flow
        false,

        // Although originally intended to indicate that clients support phone number identifiers, the scope of this
        // flag has expanded to cover phone number privacy in general
        account.isPniSupported());
  }
}
