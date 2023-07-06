/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import com.google.cloud.MetadataConfig;
import org.apache.commons.lang3.StringUtils;
import java.util.UUID;

/**
 * An instance ID property supplier attempts to supply an instance ID from cloud provider metadata, but falls back to a
 * random ID if no cloud provider instance ID is available.
 * <p>
 * In the current implementation, only <a href="https://cloud.google.com/">GCP</a> is supported, but support for other
 * platforms may be added in the future.
 */
public class InstanceIdSupplier {

  private static final String FALLBACK_INSTANCE_ID = UUID.randomUUID().toString();

  public static String getInstanceId() {
    return StringUtils.defaultIfBlank(MetadataConfig.getInstanceId(), FALLBACK_INSTANCE_ID);
  }
}
