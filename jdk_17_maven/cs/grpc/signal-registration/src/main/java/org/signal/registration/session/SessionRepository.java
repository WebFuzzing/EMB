/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session;

import com.google.i18n.phonenumbers.Phonenumber;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A session repository stores and retrieves data associated with registration sessions. Session repositories must also
 * make a best-effort to publish a {@link SessionCompletedEvent} whenever a stored session is removed from the
 * repository when its TTL expires.
 *
 * @see io.micronaut.context.event.ApplicationEventPublisher
 */
public interface SessionRepository {

  /**
   * Asynchronously stores a new registration session.
   *
   * @param phoneNumber the phone number to be verified as part of this registration session
   * @param sessionMetadata additional metadata to store as part of this session
   * @param expiration the time at which this newly-created session expires
   *
   * @return a future that yields the newly-created registration session after the session has been created and stored
   */
  CompletableFuture<RegistrationSession> createSession(Phonenumber.PhoneNumber phoneNumber,
      SessionMetadata sessionMetadata, Instant expiration);

  /**
   * Returns the registration session associated with the given session identifier.
   *
   * @param sessionId the identifier of the session to retrieve
   *
   * @return a future that yields the session stored with the given session identifier or fails with a
   * {@link SessionNotFoundException} if no session was found for the given identifier
   */
  CompletableFuture<RegistrationSession> getSession(UUID sessionId);

  /**
   * Updates the session with the given identifier with the given session update function. Updates may fail if the
   * session is not found or if multiple processes try to apply conflicting updates to the same session at the same
   * time. In the case of a conflicting update, callers should generally retry the update operation.
   * <p>
   * Note that changes to the registration's expiration timestamp should change the time when the session repository
   * evicts the session from storage.
   *
   * @param sessionId the identifier of the session to update
   * @param sessionUpdater a function that accepts an existing session and returns a new session with changes applied
   *
   * @return a future that yields the updated session when the update has been applied and stored; may fail with a
   * {@link SessionNotFoundException} if no session is found for the given identifier
   */
  CompletableFuture<RegistrationSession> updateSession(UUID sessionId,
      Function<RegistrationSession, RegistrationSession> sessionUpdater);
}
