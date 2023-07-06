/*
 * Copyright 2022-2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.twilio;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;

import javax.validation.constraints.NotBlank;

@Context
@ConfigurationProperties("twilio")
record TwilioConfiguration(@NotBlank String accountSid,
                           @NotBlank String apiKeySid,
                           @NotBlank String apiKeySecret) {

}
