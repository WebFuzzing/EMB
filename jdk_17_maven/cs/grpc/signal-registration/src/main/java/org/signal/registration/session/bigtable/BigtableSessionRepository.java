/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session.bigtable;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.ConditionalRowMutation;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.common.annotations.VisibleForTesting;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;
import org.signal.registration.metrics.MetricsUtil;
import org.signal.registration.session.RegistrationSession;
import org.signal.registration.session.SessionCompletedEvent;
import org.signal.registration.session.SessionMetadata;
import org.signal.registration.session.SessionNotFoundException;
import org.signal.registration.session.SessionRepository;
import org.signal.registration.util.CompletionExceptions;
import org.signal.registration.util.GoogleApiUtil;
import org.signal.registration.util.ReactiveResponseObserver;
import org.signal.registration.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A Bigtable session repository stores sessions in a <a href="https://cloud.google.com/bigtable">Cloud Bigtable</a>
 * table. This repository stores each session in its own row identified by the {@link ByteString} form of the session's
 * ID. This repository will periodically query for and discard expired sessions, but as a safety measure, it also
 * expects that the Bigtable column family has a garbage collection policy that will automatically remove stale sessions
 * after some amount of time.
 *
 * @see <a href="https://cloud.google.com/bigtable/docs">Cloud Bigtable documentation</a>
 * @see <a href="https://cloud.google.com/bigtable/docs/garbage-collection">About garbage collection</a>
 */
@Singleton
@Primary
@Requires(bean = BigtableDataClient.class)
class BigtableSessionRepository implements SessionRepository {

  private final BigtableDataClient bigtableDataClient;
  private final Executor executor;
  private final ApplicationEventPublisher<SessionCompletedEvent> sessionCompletedEventPublisher;
  private final BigtableSessionRepositoryConfiguration configuration;
  private final Clock clock;

  private final Timer createSessionTimer;
  private final Timer getSessionTimer;
  private final Timer updateSessionTimer;
  private final Timer deleteSessionTimer;

  @VisibleForTesting
  static final ByteString DATA_COLUMN_NAME = ByteString.copyFromUtf8("D");

  private static final ByteString REMOVAL_COLUMN_NAME = ByteString.copyFromUtf8("R");

  @VisibleForTesting
  static final Duration REMOVAL_TTL_PADDING = Duration.ofMinutes(5);

  @VisibleForTesting
  static final int MAX_UPDATE_RETRIES = 3;

  private static final ByteString EPOCH_BYTE_STRING = instantToByteString(Instant.EPOCH);

  private static final Logger logger = LoggerFactory.getLogger(BigtableSessionRepository.class);

  public BigtableSessionRepository(final BigtableDataClient bigtableDataClient,
      @Named(TaskExecutors.IO) final Executor executor,
      final ApplicationEventPublisher<SessionCompletedEvent> sessionCompletedEventPublisher,
      final BigtableSessionRepositoryConfiguration configuration,
      final Clock clock,
      final MeterRegistry meterRegistry) {

    this.bigtableDataClient = bigtableDataClient;
    this.executor = executor;
    this.sessionCompletedEventPublisher = sessionCompletedEventPublisher;
    this.configuration = configuration;
    this.clock = clock;

    this.createSessionTimer = meterRegistry.timer(MetricsUtil.name(getClass(), "createSession"));
    this.getSessionTimer = meterRegistry.timer(MetricsUtil.name(getClass(), "getSession"));
    this.updateSessionTimer = meterRegistry.timer(MetricsUtil.name(getClass(), "updateSession"));
    this.deleteSessionTimer = meterRegistry.timer(MetricsUtil.name(getClass(), "deleteSession"));
  }

  @Scheduled(fixedDelay = "${session-repository.bigtable.remove-expired-sessions-interval:10s}")
  @VisibleForTesting
  CompletableFuture<Void> deleteExpiredSessions() {
    return Flux.from(getSessionsPendingRemoval())
        .flatMap(this::removeExpiredSession)
        .doOnNext(session -> sessionCompletedEventPublisher.publishEvent(new SessionCompletedEvent(session)))
        .last()
        .toFuture()
        .thenAccept(ignored -> {});
  }

  private Publisher<RegistrationSession> getSessionsPendingRemoval() {
    return ReactiveResponseObserver.<Row>asFlux(responseObserver -> bigtableDataClient.readRowsAsync(
        Query.create(configuration.tableName())
        .filter(Filters.FILTERS.condition(Filters.FILTERS.chain()
                .filter(Filters.FILTERS.family().exactMatch(configuration.columnFamilyName()))
                .filter(Filters.FILTERS.qualifier().exactMatch(REMOVAL_COLUMN_NAME))
                .filter(Filters.FILTERS.value().range().of(EPOCH_BYTE_STRING, instantToByteString(clock.instant()))))
            .then(Filters.FILTERS.chain()
                .filter(Filters.FILTERS.pass())
                .filter(Filters.FILTERS.limit().cellsPerColumn(1)))),
        responseObserver))
        .map(row -> {
          try {
            return Optional.of(extractSession(row));
          } catch (final SessionNotFoundException e) {
            return Optional.<RegistrationSession>empty();
          }
        })
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  @VisibleForTesting
  Mono<RegistrationSession> removeExpiredSession(final RegistrationSession session) {
    final Timer.Sample sample = Timer.start();

    return Mono.fromFuture(GoogleApiUtil.toCompletableFuture(
            bigtableDataClient.checkAndMutateRowAsync(
                ConditionalRowMutation.create(configuration.tableName(), session.getId())
                    .condition(Filters.FILTERS.chain()
                        .filter(Filters.FILTERS.family().exactMatch(configuration.columnFamilyName()))
                        .filter(Filters.FILTERS.qualifier().exactMatch(DATA_COLUMN_NAME))
                        .filter(Filters.FILTERS.value().exactMatch(session.toByteString())))
                    .then(Mutation.create().deleteRow())),
            executor))
        .filter(deleted -> deleted)
        .doOnNext(ignored -> sample.stop(deleteSessionTimer))
        .map(ignored -> session);
  }

  @Override
  public CompletableFuture<RegistrationSession> createSession(final Phonenumber.PhoneNumber phoneNumber,
      final SessionMetadata sessionMetadata, final Instant expiration) {

    final Timer.Sample sample = Timer.start();

    final UUID sessionId = UUID.randomUUID();

    final RegistrationSession session = RegistrationSession.newBuilder()
        .setId(UUIDUtil.uuidToByteString(sessionId))
        .setPhoneNumber(PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
        .setCreatedEpochMillis(clock.instant().toEpochMilli())
        .setExpirationEpochMillis(expiration.toEpochMilli())
        .setSessionMetadata(sessionMetadata)
        .build();

    return GoogleApiUtil.toCompletableFuture(
            bigtableDataClient.mutateRowAsync(
                RowMutation.create(configuration.tableName(), UUIDUtil.uuidToByteString(sessionId))
                    .setCell(configuration.columnFamilyName(), DATA_COLUMN_NAME, session.toByteString())
                    .setCell(configuration.columnFamilyName(), REMOVAL_COLUMN_NAME, instantToByteString(expiration.plus(REMOVAL_TTL_PADDING)))),
            executor)
        .thenApply(ignored -> session)
        .whenComplete((ignored, throwable) -> sample.stop(createSessionTimer));
  }

  @Override
  public CompletableFuture<RegistrationSession> getSession(final UUID sessionId) {
    final Timer.Sample sample = Timer.start();

    return GoogleApiUtil.toCompletableFuture(
        bigtableDataClient.readRowAsync(configuration.tableName(),
            UUIDUtil.uuidToByteString(sessionId),
            Filters.FILTERS.limit().cellsPerColumn(1)), executor)
        .thenApply(row -> {
          try {
            final RegistrationSession registrationSession = extractSession(row);

            if (Instant.ofEpochMilli(registrationSession.getExpirationEpochMillis()).isBefore(clock.instant())) {
              throw CompletionExceptions.wrap(new SessionNotFoundException());
            }

            return registrationSession;
          } catch (final SessionNotFoundException e) {
            throw CompletionExceptions.wrap(e);
          }
        })
        .whenComplete((ignored, throwable) -> sample.stop(getSessionTimer));
  }

  @Override
  public CompletableFuture<RegistrationSession> updateSession(final UUID sessionId,
      final Function<RegistrationSession, RegistrationSession> sessionUpdater) {

    final Timer.Sample sample = Timer.start();

    return updateSession(sessionId, sessionUpdater, MAX_UPDATE_RETRIES)
        .whenComplete((ignored, throwable) -> sample.stop(updateSessionTimer));
  }

  @VisibleForTesting
  CompletableFuture<RegistrationSession> updateSession(final UUID sessionId,
      final Function<RegistrationSession, RegistrationSession> sessionUpdater,
      final int remainingRetries) {

    return getSession(sessionId)
        .thenCompose(session -> {
          final RegistrationSession updatedSession = sessionUpdater.apply(session);
          final Instant expiration = Instant.ofEpochMilli(updatedSession.getExpirationEpochMillis());

          return GoogleApiUtil.toCompletableFuture(bigtableDataClient.checkAndMutateRowAsync(
                      ConditionalRowMutation.create(configuration.tableName(), UUIDUtil.uuidToByteString(sessionId))
                          .condition(Filters.FILTERS.chain()
                              .filter(Filters.FILTERS.family().exactMatch(configuration.columnFamilyName()))
                              .filter(Filters.FILTERS.qualifier().exactMatch(DATA_COLUMN_NAME))
                              .filter(Filters.FILTERS.value().exactMatch(session.toByteString())))
                          .then(Mutation.create()
                              .setCell(configuration.columnFamilyName(), DATA_COLUMN_NAME, updatedSession.toByteString())
                              .setCell(configuration.columnFamilyName(), REMOVAL_COLUMN_NAME, instantToByteString(expiration.plus(REMOVAL_TTL_PADDING))))),
                  executor)
              .thenCompose(success -> {
                if (success) {
                  return CompletableFuture.completedFuture(updatedSession);
                } else {
                  if (remainingRetries > 0) {
                    return updateSession(sessionId, sessionUpdater, remainingRetries - 1);
                  } else {
                    return CompletableFuture.failedFuture(new RuntimeException("Retries exhausted when updating session"));
                  }
                }
              });
        });
  }

  private RegistrationSession extractSession(@Nullable final Row row) throws SessionNotFoundException {
    if (row == null) {
      throw new SessionNotFoundException();
    }

    final List<RowCell> cells = row.getCells(configuration.columnFamilyName(), DATA_COLUMN_NAME);

    if (cells.isEmpty()) {
      logger.error("Row did not contain any session data cells");
      throw new SessionNotFoundException();
    }

    try {
      return RegistrationSession.parseFrom(cells.get(0).getValue());
    } catch (final InvalidProtocolBufferException e) {
      logger.error("Failed to parse registration session", e);
      throw new UncheckedIOException(e);
    }
  }

  @VisibleForTesting
  static ByteString instantToByteString(final Instant instant) {
    final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(instant.toEpochMilli());
    buffer.flip();

    return ByteString.copyFrom(buffer);
  }
}
