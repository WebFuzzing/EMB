/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ConfigurationProperties("verification.voice")
public record VerificationVoiceConfiguration(List<@NotBlank String> supportedLanguages) {}
