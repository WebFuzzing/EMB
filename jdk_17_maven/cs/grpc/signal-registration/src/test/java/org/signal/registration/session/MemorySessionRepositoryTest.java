/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.micronaut.context.event.ApplicationEventPublisher;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.signal.registration.util.UUIDUtil;

class MemorySessionRepositoryTest extends AbstractSessionRepositoryTest {

  private ApplicationEventPublisher<SessionCompletedEvent> sessionCompletedEventPublisher;

  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();

    //noinspection unchecked
    sessionCompletedEventPublisher = mock(ApplicationEventPublisher.class);
  }

  @Override
  protected MemorySessionRepository getRepository() {
    return new MemorySessionRepository(sessionCompletedEventPublisher, getClock());
  }

  @Test
  void getSessionExpired() {
    final MemorySessionRepository repository = getRepository();

    final Instant now = Instant.now();
    when(getClock().instant()).thenReturn(now);

    final RegistrationSession createdSession = repository.createSession(PHONE_NUMBER, SESSION_METADATA, getClock().instant().plus(TTL)).join();
    final UUID sessionId = UUIDUtil.uuidFromByteString(createdSession.getId());

    final RegistrationSession expectedSession = RegistrationSession.newBuilder()
        .setId(createdSession.getId())
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164))
        .setCreatedEpochMillis(getClock().instant().toEpochMilli())
        .setExpirationEpochMillis(getClock().instant().plus(TTL).toEpochMilli())
        .setSessionMetadata(SESSION_METADATA)
        .build();

    assertEquals(expectedSession, repository.getSession(sessionId).join());

    when(getClock().instant()).thenReturn(now.plus(TTL).plus(Duration.ofSeconds(1)));

    final CompletionException completionException =
        assertThrows(CompletionException.class, () -> repository.getSession(sessionId).join());

    assertTrue(completionException.getCause() instanceof SessionNotFoundException);

    verify(sessionCompletedEventPublisher).publishEventAsync(new SessionCompletedEvent(expectedSession));
  }

  @Test
  void updateSessionExpired() {
    final MemorySessionRepository repository = getRepository();
    final String verificationCode = "123456";

    final Instant now = Instant.now();
    when(getClock().instant()).thenReturn(now);

    final Function<RegistrationSession, RegistrationSession> setVerifiedCodeFunction =
        session -> session.toBuilder().setVerifiedCode(verificationCode).build();

    final UUID sessionId = UUIDUtil.uuidFromByteString(repository.createSession(PHONE_NUMBER, SESSION_METADATA, getClock().instant().plus(TTL)).join().getId());
    repository.updateSession(sessionId, setVerifiedCodeFunction).join();

    final RegistrationSession expectedSession = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164))
        .setVerifiedCode(verificationCode)
        .setCreatedEpochMillis(getClock().instant().toEpochMilli())
        .setExpirationEpochMillis(getClock().instant().plus(TTL).toEpochMilli())
        .setSessionMetadata(SESSION_METADATA)
        .build();

    assertEquals(expectedSession, repository.getSession(sessionId).join());

    when(getClock().instant()).thenReturn(now.plus(TTL).plus(Duration.ofSeconds(1)));

    final CompletionException completionException =
        assertThrows(CompletionException.class,
            () -> repository.updateSession(sessionId, setVerifiedCodeFunction).join());

    assertTrue(completionException.getCause() instanceof SessionNotFoundException);

    verify(sessionCompletedEventPublisher).publishEventAsync(new SessionCompletedEvent(expectedSession));
  }

  @Test
  void removeExpiredSessions() {
    final MemorySessionRepository repository = getRepository();

    assertEquals(0, repository.size());

    final Instant now = Instant.now();
    when(getClock().instant()).thenReturn(now);

    final Instant expiration = getClock().instant().plus(TTL);

    final RegistrationSession session = repository.createSession(PHONE_NUMBER, SESSION_METADATA, expiration).join();

    assertEquals(1, repository.size());

    repository.removeExpiredSessions();

    assertEquals(1, repository.size(),
        "Sessions should not be removed before they have expired");

    when(getClock().instant()).thenReturn(now.plus(TTL).plus(Duration.ofSeconds(1)));

    repository.removeExpiredSessions();
    assertEquals(0, repository.size(),
        "Sessions should be removed after they have expired");

    final SessionCompletedEvent expectedEvent = new SessionCompletedEvent(RegistrationSession.newBuilder()
        .setId(session.getId())
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(PHONE_NUMBER, PhoneNumberUtil.PhoneNumberFormat.E164))
        .setCreatedEpochMillis(now.toEpochMilli())
        .setExpirationEpochMillis(expiration.toEpochMilli())
        .setSessionMetadata(SESSION_METADATA)
        .build());

    verify(sessionCompletedEventPublisher).publishEventAsync(expectedEvent);
  }
}
