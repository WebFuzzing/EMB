/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import ch.qos.logback.core.PropertyDefinerBase;
import com.google.cloud.MetadataConfig;
import org.apache.commons.lang3.StringUtils;
import java.util.UUID;

/**
 * An instance ID property definer provides an instance ID as a property for Logback appenders.
 *
 * @see <a href="https://logback.qos.ch/manual/configuration.html#definingPropsOnTheFly">Logback Manual - Chapter 3:
 * Logback configuration - Defining variables, aka properties, on the fly</a>
 *
 * @see InstanceIdSupplier
 */
@SuppressWarnings("unused")
public class InstanceIdPropertyDefiner extends PropertyDefinerBase {

  @Override
  public String getPropertyValue() {
    return InstanceIdSupplier.getInstanceId();
  }
}
