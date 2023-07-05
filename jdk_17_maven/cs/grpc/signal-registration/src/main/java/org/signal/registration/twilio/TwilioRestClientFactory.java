/*
 * Copyright 2022-2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.twilio;

import com.twilio.http.TwilioRestClient;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
class TwilioRestClientFactory {

  @Singleton
  TwilioRestClient twilioRestClient(final TwilioConfiguration configuration) {
    return new TwilioRestClient.Builder(configuration.apiKeySid(), configuration.apiKeySecret())
        .accountSid(configuration.accountSid())
        .build();
  }
}
