/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@Configuration
@Requires(env = {Environments.ANALYTICS, Environment.GOOGLE_COMPUTE})
@Requires(property = "analytics.pubsub.topic")
package org.signal.registration.analytics.gcp.pubsub;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import org.signal.registration.Environments;
