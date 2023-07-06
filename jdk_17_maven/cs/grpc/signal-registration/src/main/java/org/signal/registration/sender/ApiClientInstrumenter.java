/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.signal.registration.metrics.MetricsUtil;

@Singleton
public class ApiClientInstrumenter {

  private static final String CALL_COUNTER_NAME = MetricsUtil.name(ApiClientInstrumenter.class, "apiCalls");
  private static final String CALL_TIMER_NAME = MetricsUtil.name(ApiClientInstrumenter.class, "apiCallDuration");

  private static final String ENDPOINT_TAG_NAME = "endpoint";
  private static final String ERROR_CODE_TAG_NAME = "code";

  private final MeterRegistry meterRegistry;

  public ApiClientInstrumenter(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public void recordApiCallMetrics(
      final String senderName,
      final String endpointName,
      final boolean success,
      @Nullable final String errorCode,
      final Timer.Sample timerSample) {

    final List<Tag> tags = new ArrayList<>(4);
    tags.add(Tag.of(ENDPOINT_TAG_NAME, endpointName));
    tags.add(Tag.of(MetricsUtil.SENDER_TAG_NAME, senderName));
    tags.add(Tag.of(MetricsUtil.SUCCESS_TAG_NAME, String.valueOf(success)));

    Optional
        .ofNullable(errorCode)
        .ifPresent(s -> tags.add(Tag.of(ERROR_CODE_TAG_NAME, s)));

    meterRegistry.counter(CALL_COUNTER_NAME, tags).increment();
    timerSample.stop(meterRegistry.timer(CALL_TIMER_NAME, ENDPOINT_TAG_NAME, endpointName, MetricsUtil.SENDER_TAG_NAME, senderName));
  }

}
