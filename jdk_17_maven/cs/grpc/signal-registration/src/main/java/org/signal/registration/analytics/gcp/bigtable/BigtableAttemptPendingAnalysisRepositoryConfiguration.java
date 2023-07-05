/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics.gcp.bigtable;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import javax.validation.constraints.NotBlank;

@Context
@ConfigurationProperties("analytics.bigtable")
public record BigtableAttemptPendingAnalysisRepositoryConfiguration(@NotBlank String tableId,
                                                                    @NotBlank String columnFamilyName) {

}
