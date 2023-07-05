/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.ratelimit;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A rate limiter limits the number of times in some period of time that an action identified by a given key may be
 * taken.
 *
 * @param <K> the type of key that identifies a rate-limited action
 */
public interface RateLimiter<K> {

  /**
   * Returns the next time at which this rate limiter will permit the rate-limited action to be taken. If the returned
   * time is before or equal to the current time, the caller may take the action immediately. Calling this method must
   * not change the rate limiter's state (i.e. callers must not be penalized for calling this method).
   *
   * @param key a key that identifies the action to be taken
   * @return the next time at which this rate limiter will permit the rate-limited action to be taken; if equal to or
   * before the current time, the action may be taken immediately, and if empty, no amount of simply waiting will allow
   * the action to succeed and callers may need to take some other action to proceed
   */
  CompletableFuture<Optional<Instant>> getTimeOfNextAction(K key);

  /**
   * Checks whether the action identified by the given key may be taken.
   *
   * @param key a key that identifies the action to be taken
   *
   * @return a future that either completes normally (in which case the action is permitted) or fails with a
   * {@link RateLimitExceededException} if the caller must wait before taking the action governed by this rate limiter
   */
  CompletableFuture<Void> checkRateLimit(K key);
}
