/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.session.bigtable;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("session-repository.bigtable")
record BigtableSessionRepositoryConfiguration(@NotBlank String tableName,
                                              @NotBlank String columnFamilyName) {

}
