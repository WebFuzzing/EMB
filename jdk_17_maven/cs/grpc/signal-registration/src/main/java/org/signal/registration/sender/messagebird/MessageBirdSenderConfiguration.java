/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.messagebird;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Context
@ConfigurationProperties("messagebird")
public record MessageBirdSenderConfiguration(
    @NotBlank @NotNull String defaultSenderId,
    Map<@NotBlank String, @NotBlank String> regionSenderIds) {}
