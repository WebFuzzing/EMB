/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UUIDUtilTest {

  @Test
  void uuidToFromByteString() {
    final UUID uuid = UUID.randomUUID();

    assertEquals(uuid, UUIDUtil.uuidFromByteString(UUIDUtil.uuidToByteString(uuid)));
  }
}
