/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.messagebird.verify;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.bind.annotation.Bindable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * @param sessionTtl How long verification sessions are valid for
 */
@Context
@ConfigurationProperties("messagebird.verify")
public record MessageBirdVerifyConfiguration(@Bindable(defaultValue = "PT10M") @NotNull Duration sessionTtl) {}
