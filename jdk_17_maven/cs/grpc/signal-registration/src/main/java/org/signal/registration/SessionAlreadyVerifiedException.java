/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration;

import org.signal.registration.session.RegistrationSession;

/**
 * Indicates that a verification code could not be sent for a given session because that session has already been
 * verified.
 */
public class SessionAlreadyVerifiedException extends NoStackTraceException {

  private final RegistrationSession registrationSession;

  public SessionAlreadyVerifiedException(final RegistrationSession registrationSession) {
    this.registrationSession = registrationSession;
  }

  public RegistrationSession getRegistrationSession() {
    return registrationSession;
  }
}
