/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.gcp;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;


@Requires(env = Environment.GOOGLE_COMPUTE)
@Factory
class FirestoreClientFactory {

  @Singleton
  Firestore firestore() {
    return FirestoreOptions.getDefaultInstance().toBuilder()
        .build()
        .getService();
  }
}
