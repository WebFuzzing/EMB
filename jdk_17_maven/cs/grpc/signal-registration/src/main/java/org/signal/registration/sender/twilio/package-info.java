/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@Configuration
@Requires(property = "twilio.account-sid")
@Requires(property = "twilio.api-key-sid")
@Requires(property = "twilio.api-key-secret")
package org.signal.registration.sender.twilio;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
