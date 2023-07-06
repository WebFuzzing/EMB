/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@Configuration
@Requires(property = "gcp.bigtable.project-id")
@Requires(property = "gcp.bigtable.instance-id")
@Requires(property = "analytics.bigtable.table-id")
@Requires(property = "analytics.bigtable.column-family-name")
package org.signal.registration.analytics.gcp.bigtable;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
