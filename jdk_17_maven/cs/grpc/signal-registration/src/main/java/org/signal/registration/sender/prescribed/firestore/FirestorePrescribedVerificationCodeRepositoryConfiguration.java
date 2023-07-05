/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.prescribed.firestore;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("prescribed-verification-codes.firestore")
class FirestorePrescribedVerificationCodeRepositoryConfiguration {

  private String collectionName;

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(final String collectionName) {
    this.collectionName = collectionName;
  }
}
