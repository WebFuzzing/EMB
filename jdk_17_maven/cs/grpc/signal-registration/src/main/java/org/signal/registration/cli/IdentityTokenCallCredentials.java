/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.cli;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import java.util.concurrent.Executor;

/**
 * Identity token call credentials present an OIDC identity token to a gRPC server via the {@code Authorization}
 * {@link Metadata} key.
 */
class IdentityTokenCallCredentials extends CallCredentials {

  private final Metadata authorizationMetadata;

  IdentityTokenCallCredentials(final String bearerToken) {
    authorizationMetadata = new Metadata();
    authorizationMetadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + bearerToken);
  }

  @Override
  public void applyRequestMetadata(final RequestInfo requestInfo,
      final Executor appExecutor,
      final MetadataApplier applier) {

    applier.apply(authorizationMetadata);
  }

  @Override
  public void thisUsesUnstableApi() {
  }
}
