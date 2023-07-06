/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GoogleApiUtil {

  public static <T> CompletableFuture<T> toCompletableFuture(final ApiFuture<T> apiFuture, final Executor executor) {
    final CompletableFuture<T> completableFuture = new CompletableFuture<>();

    ApiFutures.addCallback(apiFuture, new ApiFutureCallback<>() {
      @Override
      public void onSuccess(final T value) {
        completableFuture.complete(value);
      }

      @Override
      public void onFailure(final Throwable throwable) {
        completableFuture.completeExceptionally(throwable);
      }
    }, executor);

    return completableFuture;
  }

  public static Timestamp timestampFromInstant(final Instant instant) {
    return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
  }

  public static Instant instantFromTimestamp(final Timestamp timestamp) {
    return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
  }
}
