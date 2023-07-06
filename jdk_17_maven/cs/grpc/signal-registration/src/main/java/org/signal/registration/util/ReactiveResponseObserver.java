/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import java.util.function.Consumer;

/**
 * A reactive response observer wraps a {@link ResponseObserver} from a Google RPC call in a {@link Flux}.
 */
public class ReactiveResponseObserver<T> implements ResponseObserver<T> {

  private final FluxSink<T> sink;
  private StreamController streamController;

  /**
   * Creates a new response observer, immediately wrapping it in a {@code Flux}.
   *
   * @param initializer a consumer that initializes the response observer, presumably by passing it to an RPC
   *
   * @return a {@code Flux} over the responses passed to the {@link ResponseObserver}
   *
   * @param <T> the type of response produced by the response observer
   */
  public static <T> Flux<T> asFlux(final Consumer<ReactiveResponseObserver<T>> initializer) {
    return Flux.create(sink -> {
      final ReactiveResponseObserver<T> responseObserver = new ReactiveResponseObserver<T>(sink);
      sink.onDispose(responseObserver::cancel);

      initializer.accept(responseObserver);
    });
  }

  private ReactiveResponseObserver(final FluxSink<T> sink) {
    this.sink = sink;
  }

  @Override
  public void onStart(final StreamController streamController) {
    this.streamController = streamController;
  }

  @Override
  public void onResponse(final T response) {
    sink.next(response);
  }

  @Override
  public void onError(final Throwable throwable) {
    sink.error(throwable);
  }

  @Override
  public void onComplete() {
    sink.complete();
  }

  void cancel() {
    if (streamController != null) {
      streamController.cancel();
    }
  }
}
