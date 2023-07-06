/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.messagebird;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdServiceImpl;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class MessageBirdClientFactory {

  @Singleton
  MessageBirdClient messageBirdClient(final MessageBirdClientConfiguration configuration) {
    return new MessageBirdClient(new MessageBirdServiceImpl(configuration.accessKey()));
  }
}
