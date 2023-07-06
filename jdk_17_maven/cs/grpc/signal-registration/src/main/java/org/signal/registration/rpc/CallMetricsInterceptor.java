/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.rpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micronaut.core.order.Ordered;
import jakarta.inject.Singleton;
import org.signal.registration.metrics.MetricsUtil;

@Singleton
public class CallMetricsInterceptor implements ServerInterceptor, Ordered {

  private final MeterRegistry meterRegistry;

  private static final String RPC_CALL_TIMER_NAME = MetricsUtil.name(CallMetricsInterceptor.class, "rpcCallDuration");
  private static final String RPC_CALL_COUNTER_NAME = MetricsUtil.name(CallMetricsInterceptor.class, "rpcCalls");

  private static final String METHOD_TAG_NAME = "method";
  private static final String STATUS_TAG_NAME = "status";

  private static final Context.Key<Timer.Sample> CONTEXT_TIMER_SAMPLE_KEY = Context.key("timerSample");

  public CallMetricsInterceptor(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
      final Metadata headers,
      final ServerCallHandler<ReqT, RespT> next) {

    final Context context = Context.current().withValue(CONTEXT_TIMER_SAMPLE_KEY, Timer.start());

    return Contexts.interceptCall(context, new ForwardingServerCall.SimpleForwardingServerCall<>(call) {

      @Override
      public void close(final Status status, final Metadata trailers) {
        super.close(status, trailers);

        Tags tags = Tags.of(
            METHOD_TAG_NAME, getMethodDescriptor().getBareMethodName(),
            STATUS_TAG_NAME, status.getCode().name());

        meterRegistry.counter(RPC_CALL_COUNTER_NAME, tags).increment();

        CONTEXT_TIMER_SAMPLE_KEY.get().stop(
            meterRegistry.timer(RPC_CALL_TIMER_NAME, METHOD_TAG_NAME, getMethodDescriptor().getBareMethodName()));
      }
    }, headers, next);
  }
}
