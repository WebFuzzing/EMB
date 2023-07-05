/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.fictitious.firestore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.signal.registration.util.GoogleApiUtil;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class FirestoreFictitiousNumberVerificationCodeRepositoryTest {

  private Firestore firestore;
  private FirestoreFictitiousNumberVerificationCodeRepository repository;

  // Timestamps in Firestore are only precise down to microseconds;
  // see https://firebase.google.com/docs/firestore/manage-data/data-types.
  private static final Instant CURRENT_TIME = Instant.now().truncatedTo(ChronoUnit.MICROS);

  private static final String FIRESTORE_EMULATOR_IMAGE_NAME =
      "gcr.io/google.com/cloudsdktool/cloud-sdk:" + System.getProperty("firestore.emulator.version", "emulators");

  @Container
  private static final FirestoreEmulatorContainer CONTAINER =
      new FirestoreEmulatorContainer(DockerImageName.parse(FIRESTORE_EMULATOR_IMAGE_NAME));

  private static final String COLLECTION_NAME = "fictitious-numbers";
  private static final String EXPIRATION_FIELD_NAME = "expiration";

  @BeforeEach
  void setUp() {
    final FirestoreFictitiousNumberVerificationCodeRepositoryConfiguration configuration =
        new FirestoreFictitiousNumberVerificationCodeRepositoryConfiguration();

    configuration.setCollectionName(COLLECTION_NAME);
    configuration.setExpirationFieldName(EXPIRATION_FIELD_NAME);

    firestore = FirestoreOptions.getDefaultInstance().toBuilder()
        .setHost(CONTAINER.getEmulatorEndpoint())
        .setCredentials(NoCredentials.getInstance())
        .setProjectId("firestore-fictitious-numbers-test")
        .build()
        .getService();

    repository = new FirestoreFictitiousNumberVerificationCodeRepository(firestore,
        MoreExecutors.directExecutor(),
        configuration,
        Clock.fixed(CURRENT_TIME, ZoneId.systemDefault()),
        new SimpleMeterRegistry());
  }

  @Test
  void storeVerificationCode() throws ExecutionException, InterruptedException {
    final Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().getExampleNumber("US");
    final String verificationCode = "765432";
    final Duration ttl = Duration.ofMinutes(17);

    repository.storeVerificationCode(phoneNumber, verificationCode, ttl).join();

    final DocumentSnapshot documentSnapshot = firestore.collection(COLLECTION_NAME)
        .document(PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
        .get()
        .get();

    assertEquals(verificationCode,
        documentSnapshot.get(FirestoreFictitiousNumberVerificationCodeRepository.VERIFICATION_CODE_KEY));

    assertEquals(GoogleApiUtil.timestampFromInstant(CURRENT_TIME.plus(ttl)),
        documentSnapshot.get(EXPIRATION_FIELD_NAME));
  }
}
