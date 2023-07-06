/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.fictitious.firestore;

import io.micronaut.context.annotation.ConfigurationProperties;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties("fictitious-numbers.firestore")
class FirestoreFictitiousNumberVerificationCodeRepositoryConfiguration {

  @NotBlank
  private String collectionName;

  @NotBlank
  private String expirationFieldName;

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(final String collectionName) {
    this.collectionName = collectionName;
  }

  public String getExpirationFieldName() {
    return expirationFieldName;
  }

  public void setExpirationFieldName(final String expirationFieldName) {
    this.expirationFieldName = expirationFieldName;
  }
}
