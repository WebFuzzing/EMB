/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration;

import org.signal.registration.session.RegistrationSession;

/**
 * Indicates that a verification code could not be checked in the scope of a particular session because no verification
 * codes have been sent in the scope of that session.
 */
public class NoVerificationCodeSentException extends NoStackTraceException {

  private final RegistrationSession registrationSession;

  public NoVerificationCodeSentException(final RegistrationSession registrationSession) {
    this.registrationSession = registrationSession;
  }

  public RegistrationSession getRegistrationSession() {
    return registrationSession;
  }
}
