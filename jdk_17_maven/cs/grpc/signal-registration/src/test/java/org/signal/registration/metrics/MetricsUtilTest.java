/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsUtilTest {

  @Test
  void nameFromClass() {
    assertEquals("registration.MetricsUtilTest.test", MetricsUtil.name(getClass(), "test"));
  }
}
