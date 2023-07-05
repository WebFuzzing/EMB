/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.signal.registration.sender.VerificationCodeGenerator;

class VerificationCodeGeneratorTest {

  @Test
  void generateVerificationCode() {
    for (int i = 0; i < 1024; i++) {
      assertEquals(6, new VerificationCodeGenerator().generateVerificationCode().length());
    }
  }
}
