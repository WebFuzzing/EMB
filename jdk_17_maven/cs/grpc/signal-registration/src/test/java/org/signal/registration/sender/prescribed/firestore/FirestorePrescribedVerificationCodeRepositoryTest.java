/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.prescribed.firestore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class FirestorePrescribedVerificationCodeRepositoryTest {

  private Firestore firestore;
  private FirestorePrescribedVerificationCodeRepository repository;


  private static final String FIRESTORE_EMULATOR_IMAGE_NAME = "gcr.io/google.com/cloudsdktool/cloud-sdk:" +
      System.getProperty("firestore.emulator.version", "emulators");

  @Container
  private static final FirestoreEmulatorContainer CONTAINER = new FirestoreEmulatorContainer(
      DockerImageName.parse(FIRESTORE_EMULATOR_IMAGE_NAME));

  private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

  private static final String COLLECTION_NAME = "prescribed-verification-codes";

  @BeforeEach
  void setUp() {
    final FirestorePrescribedVerificationCodeRepositoryConfiguration configuration = new FirestorePrescribedVerificationCodeRepositoryConfiguration();
    configuration.setCollectionName(COLLECTION_NAME);

    firestore = FirestoreOptions.getDefaultInstance().toBuilder()
        .setHost(CONTAINER.getEmulatorEndpoint())
        .setCredentials(NoCredentials.getInstance())
        .setProjectId("firestore-prescribed-verification-codes-test")
        .build()
        .getService();

    repository = new FirestorePrescribedVerificationCodeRepository(firestore,
        MoreExecutors.directExecutor(),
        configuration,
        new SimpleMeterRegistry());
  }

  @Test
  void getVerificationCodes() throws ExecutionException, InterruptedException {
    final Phonenumber.PhoneNumber firstNumber = PHONE_NUMBER_UTIL.getExampleNumber("US");
    final Phonenumber.PhoneNumber secondNumber = PHONE_NUMBER_UTIL.getExampleNumber("MX");

    final Map<Phonenumber.PhoneNumber, String> expectedVerificationCodes = Map.of(
        firstNumber, "123456",
        secondNumber, "987654");

    // Insert a properly-formatted E.164 phone number
    firestore.collection(COLLECTION_NAME)
        .document(PHONE_NUMBER_UTIL.format(firstNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
        .set(Map.of(FirestorePrescribedVerificationCodeRepository.VERIFICATION_CODE_KEY, expectedVerificationCodes.get(firstNumber)))
        .get();

    // Insert a mostly-E.164-formatted phone number, but without the leading '+'
    firestore.collection(COLLECTION_NAME)
        .document(PHONE_NUMBER_UTIL.format(secondNumber, PhoneNumberUtil.PhoneNumberFormat.E164).substring(1))
        .set(Map.of(FirestorePrescribedVerificationCodeRepository.VERIFICATION_CODE_KEY, expectedVerificationCodes.get(secondNumber)))
        .get();

    // Insert a document with a completely bogus ID
    firestore.collection(COLLECTION_NAME)
        .document("not-a-phone-number")
        .set(Map.of(FirestorePrescribedVerificationCodeRepository.VERIFICATION_CODE_KEY, "987654"))
        .get();

    // Insert a document with a valid ID, but unexpected value
    firestore.collection(COLLECTION_NAME)
        .document(PHONE_NUMBER_UTIL.format(PHONE_NUMBER_UTIL.getExampleNumber("CA"), PhoneNumberUtil.PhoneNumberFormat.E164))
        .set(Map.of(FirestorePrescribedVerificationCodeRepository.VERIFICATION_CODE_KEY + "-incorrect", "987654"))
        .get();

    assertEquals(expectedVerificationCodes, repository.getVerificationCodes().join());
  }
}
