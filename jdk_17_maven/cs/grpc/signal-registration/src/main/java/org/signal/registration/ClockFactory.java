/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import java.time.Clock;

@Factory
public class ClockFactory {

  @Singleton
  public Clock getClock() {
    return Clock.systemUTC();
  }
}
