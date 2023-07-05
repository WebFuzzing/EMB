/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FirestoreTestUtil {

  private FirestoreTestUtil() {}

  public static void clearFirestoreDatabase(final FirestoreEmulatorContainer container,
      final String projectId) throws IOException {

    // See https://firebase.google.com/docs/emulator-suite/connect_firestore#clear_your_database_between_tests
    try {
      final URI clearDatabaseUri = new URI("http", null, container.getHost(), container.getMappedPort(8080), "/emulator/v1/projects/" + projectId + "/databases/(default)/documents", null, null);
      final HttpResponse<String> response = HttpClient.newHttpClient().send(
          HttpRequest.newBuilder(clearDatabaseUri).DELETE().build(),
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new IOException("Failed to clear Firestore database; response status " + response.statusCode());
      }
    } catch (final URISyntaxException e) {
      // This should never happen for a literally-constructed URI
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      throw new IOException("Interrupted while clearing Firestore database", e);
    }
  }

  public static Firestore buildFirestoreClient(final FirestoreEmulatorContainer container, final String projectId) {
    return FirestoreOptions.getDefaultInstance().toBuilder()
        .setHost(container.getEmulatorEndpoint())
        .setCredentials(NoCredentials.getInstance())
        .setProjectId(projectId)
        .build()
        .getService();
  }
}
