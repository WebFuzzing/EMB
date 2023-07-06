/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import jakarta.inject.Singleton;
import java.security.SecureRandom;

/**
 * A generator that produces random six-digit verification codes for use with
 * {@link org.signal.registration.sender.VerificationCodeSender} implementations that use locally-generated verification
 * codes.
 */
@Singleton
public class VerificationCodeGenerator {

  public String generateVerificationCode() {
    return String.format("%06d", new SecureRandom().nextInt(1_000_000));
  }
}
