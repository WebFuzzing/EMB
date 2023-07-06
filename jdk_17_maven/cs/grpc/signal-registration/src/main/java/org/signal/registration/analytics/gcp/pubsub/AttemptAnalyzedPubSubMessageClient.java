/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.gcp.pubsub;

import io.micronaut.gcp.pubsub.annotation.PubSubClient;
import io.micronaut.gcp.pubsub.annotation.Topic;

@PubSubClient
public interface AttemptAnalyzedPubSubMessageClient {

  @Topic(value = "${analytics.pubsub.topic}", contentType = "application/protobuf")
  void send(byte[] attemptAnalyzedPubSubMessage);
}
