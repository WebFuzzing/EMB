/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session.bigtable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.emulator.v2.Emulator;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.protobuf.ByteString;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micronaut.context.event.ApplicationEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.signal.registration.session.AbstractSessionRepositoryTest;
import org.signal.registration.session.RegistrationSession;
import org.signal.registration.session.SessionCompletedEvent;
import org.signal.registration.session.SessionMetadata;
import org.signal.registration.session.SessionNotFoundException;
import org.signal.registration.session.SessionRepository;
import org.signal.registration.util.CompletionExceptions;
import org.signal.registration.util.UUIDUtil;

class BigtableSessionRepositoryTest extends AbstractSessionRepositoryTest {

  private Emulator emulator;
  private BigtableDataClient bigtableDataClient;
  private ExecutorService executorService;
  private ApplicationEventPublisher<SessionCompletedEvent> sessionCompletedEventPublisher;

  private BigtableSessionRepository sessionRepository;

  private static final String PROJECT_ID = "test";
  private static final String INSTANCE_ID = "test";

  private static final String TABLE_ID = "sessions";
  private static final String COLUMN_FAMILY_NAME = "S";

  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();

    emulator = Emulator.createBundled();
    emulator.start();

    try (final BigtableTableAdminClient tableAdminClient =
        BigtableTableAdminClient.create(BigtableTableAdminSettings.newBuilderForEmulator(emulator.getPort())
            .setProjectId(PROJECT_ID)
            .setInstanceId(INSTANCE_ID)
            .build())) {

      tableAdminClient.createTable(CreateTableRequest.of(TABLE_ID).addFamily(COLUMN_FAMILY_NAME));
    }

    bigtableDataClient = BigtableDataClient.create(BigtableDataSettings.newBuilderForEmulator(emulator.getPort())
        .setProjectId(PROJECT_ID)
        .setInstanceId(INSTANCE_ID)
        .build());

    executorService = Executors.newSingleThreadExecutor();

    //noinspection unchecked
    sessionCompletedEventPublisher = mock(ApplicationEventPublisher.class);

    sessionRepository = new BigtableSessionRepository(bigtableDataClient,
        executorService,
        sessionCompletedEventPublisher, new BigtableSessionRepositoryConfiguration(TABLE_ID, COLUMN_FAMILY_NAME),
        getClock(),
        new SimpleMeterRegistry());
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    bigtableDataClient.close();
    emulator.stop();

    executorService.shutdown();

    //noinspection ResultOfMethodCallIgnored
    executorService.awaitTermination(1, TimeUnit.SECONDS);
  }

  @Override
  protected SessionRepository getRepository() {
    return sessionRepository;
  }

  @Test
  void getSessionExpired() {
    final RegistrationSession expiredSession = sessionRepository.createSession(
        PhoneNumberUtil.getInstance().getExampleNumber("US"),
        SessionMetadata.newBuilder().build(),
        getClock().instant().minusSeconds(1)).join();

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> sessionRepository.getSession(UUIDUtil.uuidFromByteString(expiredSession.getId())).join());

    assertTrue(CompletionExceptions.unwrap(completionException) instanceof SessionNotFoundException);
  }

  @Test
  void updateSessionWithRetry() {
    final BigtableDataClient mockBigtableClient = mock(BigtableDataClient.class);

    final BigtableSessionRepository retryRepository = new BigtableSessionRepository(mockBigtableClient,
        executorService,
        sessionCompletedEventPublisher, new BigtableSessionRepositoryConfiguration(TABLE_ID, COLUMN_FAMILY_NAME),
        getClock(),
        new SimpleMeterRegistry());

    final UUID sessionId = UUID.randomUUID();

    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setExpirationEpochMillis(getClock().instant().plusSeconds(1).toEpochMilli())
        .build();

    final Row row = buildRowForSession(session);

    when(mockBigtableClient.readRowAsync(anyString(), any(ByteString.class), any()))
        .thenReturn(ApiFutures.immediateFuture(row));

    when(mockBigtableClient.checkAndMutateRowAsync(any()))
        .thenReturn(ApiFutures.immediateFuture(false))
        .thenReturn(ApiFutures.immediateFuture(true));

    assertDoesNotThrow(() -> retryRepository.updateSession(sessionId, s -> s, 2).join());

    verify(mockBigtableClient, times(2)).checkAndMutateRowAsync(any());
  }

  @Test
  void updateSessionRetriesExhausted() {
    final BigtableDataClient mockBigtableClient = mock(BigtableDataClient.class);

    final BigtableSessionRepository retryRepository = new BigtableSessionRepository(mockBigtableClient,
        executorService,
        sessionCompletedEventPublisher,
        new BigtableSessionRepositoryConfiguration(TABLE_ID, COLUMN_FAMILY_NAME),
        getClock(), new SimpleMeterRegistry());

    final UUID sessionId = UUID.randomUUID();

    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setExpirationEpochMillis(getClock().instant().plusSeconds(1).toEpochMilli())
        .build();

    final Row row = buildRowForSession(session);

    when(mockBigtableClient.readRowAsync(anyString(), any(ByteString.class), any()))
        .thenReturn(ApiFutures.immediateFuture(row));

    when(mockBigtableClient.checkAndMutateRowAsync(any()))
        .thenReturn(ApiFutures.immediateFuture(false));

    assertThrows(RuntimeException.class,
        () -> retryRepository.updateSession(sessionId, s -> s, 3).join());

    // We expect one call for the initial attempt, then one for each retry
    verify(mockBigtableClient, times(BigtableSessionRepository.MAX_UPDATE_RETRIES + 1)).checkAndMutateRowAsync(any());
  }

  private Row buildRowForSession(final RegistrationSession session) {
    final Row row = mock(Row.class);

    final RowCell expirationCell = mock(RowCell.class);

    final Instant currentTime = getClock().instant();
    when(expirationCell.getValue())
        .thenReturn(BigtableSessionRepository.instantToByteString(currentTime.plus(Duration.ofSeconds(1))));

    final RowCell sessionDataCell = mock(RowCell.class);
    when(sessionDataCell.getValue()).thenReturn(session.toByteString());

    when(row.getCells(COLUMN_FAMILY_NAME, BigtableSessionRepository.DATA_COLUMN_NAME))
        .thenReturn(List.of(sessionDataCell));

    return row;
  }

  @Test
  void deleteExpiredSessions() {
    final Instant currentTime = getClock().instant();

    final RegistrationSession expiredSession = sessionRepository.createSession(
        PhoneNumberUtil.getInstance().getExampleNumber("US"),
        SessionMetadata.newBuilder().build(),
        currentTime.minus(BigtableSessionRepository.REMOVAL_TTL_PADDING.multipliedBy(2))).join();

    // Add a non-expired session which should not appear in the list of expired sessions
    sessionRepository.createSession(
        PhoneNumberUtil.getInstance().getExampleNumber("GB"),
        SessionMetadata.newBuilder().build(),
        currentTime.plus(BigtableSessionRepository.REMOVAL_TTL_PADDING.multipliedBy(2))).join();

    sessionRepository.deleteExpiredSessions().join();

    final CompletionException completionException = assertThrows(CompletionException.class,
        () -> sessionRepository.getSession(UUIDUtil.uuidFromByteString(expiredSession.getId())).join());

    assertTrue(CompletionExceptions.unwrap(completionException) instanceof SessionNotFoundException);

    verify(sessionCompletedEventPublisher).publishEvent(new SessionCompletedEvent(expiredSession));
  }

  @Test
  void removeExpiredSession() {
    final RegistrationSession expiredSession = sessionRepository.createSession(
        PhoneNumberUtil.getInstance().getExampleNumber("US"),
        SessionMetadata.newBuilder().build(),
        Instant.now().minus(BigtableSessionRepository.REMOVAL_TTL_PADDING.multipliedBy(2))).join();

    final RegistrationSession notInRepositorySession = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(UUID.randomUUID()))
        .build();

    assertEquals(Optional.of(expiredSession), sessionRepository.removeExpiredSession(expiredSession).blockOptional());
    assertEquals(Optional.empty(), sessionRepository.removeExpiredSession(notInRepositorySession).blockOptional());
  }
}
