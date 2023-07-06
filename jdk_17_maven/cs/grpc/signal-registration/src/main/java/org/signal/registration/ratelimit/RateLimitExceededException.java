/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.ratelimit;

import org.signal.registration.NoStackTraceException;
import org.signal.registration.session.RegistrationSession;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;

/**
 * Indicates that some action was not permitted because one or more callers have attempted the action too frequently.
 * Callers that receive this exception may retry the action after the time indicated by
 * {@link #getRetryAfterDuration()}.
 */
public class RateLimitExceededException extends NoStackTraceException {

  @Nullable
  private final Duration retryAfterDuration;

  @Nullable
  private final RegistrationSession registrationSession;

  public RateLimitExceededException(final Duration retryAfterDuration) {
    this(retryAfterDuration, null);
  }

  public RateLimitExceededException(@Nullable final Duration retryAfterDuration,
      @Nullable final RegistrationSession registrationSession) {

    this.retryAfterDuration = retryAfterDuration;
    this.registrationSession = registrationSession;
  }

  /**
   * Returns the next time at which the action blocked by this exception might succeed.
   *
   * @return the next time at which the action blocked by this exception might succeed; if empty, no amount of simply
   * waiting will allow the action to succeed and callers may need to take some other action to proceed
   */
  public Optional<Duration> getRetryAfterDuration() {
    return Optional.ofNullable(retryAfterDuration);
  }

  /**
   * Returns the registration session associated with this exception, if any; associated registration sessions may
   * contain additional rate-limiting information.
   *
   * @return the registration associated with this exception, if any
   */
  public Optional<RegistrationSession> getRegistrationSession() {
    return Optional.ofNullable(registrationSession);
  }
}
