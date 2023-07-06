/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.signal.registration.sender.messagebird.classic;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.bind.annotation.Bindable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

/**
 * @param messageRepeatCount The number of times the TTS call should repeat the verification message
 * @param sessionTtl How long verification sessions are valid for
 * @param supportedLanguages Languages that messagebird voice supports as translation targets
 */
@Context
@ConfigurationProperties("messagebird.voice")
public record MessageBirdVoiceConfiguration(
    @Bindable(defaultValue = "3") @NotNull int messageRepeatCount,
    @Bindable(defaultValue = "PT10M") @NotNull Duration sessionTtl,
    List<@NotBlank String>supportedLanguages) {

}
