/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@Configuration
@Requires(bean = MessageBirdClient.class)
@Requires(bean = AttemptPendingAnalysisRepository.class)
@Requires(env = Environments.ANALYTICS)
package org.signal.registration.analytics.messagebird;

import com.messagebird.MessageBirdClient;
import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
import org.signal.registration.Environments;
import org.signal.registration.analytics.AttemptPendingAnalysisRepository;
