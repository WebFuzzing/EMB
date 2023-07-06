/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@Configuration
@Requires(notEnv = Environments.ANALYTICS)
package org.signal.registration.rpc;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
import org.signal.registration.Environments;
