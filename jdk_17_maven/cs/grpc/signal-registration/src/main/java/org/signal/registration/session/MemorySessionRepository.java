/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session;

import com.google.common.annotations.VisibleForTesting;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.signal.registration.Environments;
import org.signal.registration.util.UUIDUtil;

@Singleton
@Requires(env = {Environments.DEVELOPMENT, Environment.TEST})
@Requires(missingBeans = SessionRepository.class)
public class MemorySessionRepository implements SessionRepository {

  private final ApplicationEventPublisher<SessionCompletedEvent> sessionCompletedEventPublisher;
  private final Clock clock;

  private final Map<UUID, RegistrationSession> sessionsById = new ConcurrentHashMap<>();

  public MemorySessionRepository(final ApplicationEventPublisher<SessionCompletedEvent> sessionCompletedEventPublisher,
      final Clock clock) {

    this.sessionCompletedEventPublisher = sessionCompletedEventPublisher;
    this.clock = clock;
  }

  @Scheduled(fixedRate = "10s")
  @VisibleForTesting
  void removeExpiredSessions() {
    final Instant now = clock.instant();

    final List<UUID> expiredSessionIds = sessionsById.entrySet().stream()
        .filter(entry -> now.isAfter(Instant.ofEpochMilli(entry.getValue().getExpirationEpochMillis())))
        .map(Map.Entry::getKey)
        .toList();

    expiredSessionIds.forEach(sessionId -> sessionCompletedEventPublisher.publishEventAsync(
        new SessionCompletedEvent(sessionsById.remove(sessionId))));
  }

  @Override
  public CompletableFuture<RegistrationSession> createSession(final Phonenumber.PhoneNumber phoneNumber,
      final SessionMetadata sessionMetadata,
      final Instant expiration) {

    final UUID sessionId = UUID.randomUUID();
    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
        .setCreatedEpochMillis(clock.instant().toEpochMilli())
        .setExpirationEpochMillis(expiration.toEpochMilli())
        .setSessionMetadata(sessionMetadata)
        .build();

    sessionsById.put(sessionId, session);

    return CompletableFuture.completedFuture(session);
  }

  @Override
  public CompletableFuture<RegistrationSession> getSession(final UUID sessionId) {
    final RegistrationSession session = sessionsById.computeIfPresent(sessionId, (id, existingSession) -> {
          if (clock.instant().isAfter(Instant.ofEpochMilli(existingSession.getExpirationEpochMillis()))) {
            sessionCompletedEventPublisher.publishEventAsync(new SessionCompletedEvent(existingSession));
            return null;
          } else {
            return existingSession;
          }
        });

    return session != null ?
        CompletableFuture.completedFuture(session) :
        CompletableFuture.failedFuture(new SessionNotFoundException());
  }

  @Override
  public CompletableFuture<RegistrationSession> updateSession(final UUID sessionId,
      final Function<RegistrationSession, RegistrationSession> sessionUpdater) {

    final RegistrationSession updatedSession =
        sessionsById.computeIfPresent(sessionId, (id, existingSession) -> {
          if (clock.instant().isAfter(Instant.ofEpochMilli(existingSession.getExpirationEpochMillis()))) {
            sessionCompletedEventPublisher.publishEventAsync(new SessionCompletedEvent(existingSession));
            return null;
          } else {
            return sessionUpdater.apply(existingSession);
          }
        });

    return updatedSession != null ?
        CompletableFuture.completedFuture(updatedSession) :
        CompletableFuture.failedFuture(new SessionNotFoundException());
  }

  @VisibleForTesting
  int size() {
    return sessionsById.size();
  }
}
