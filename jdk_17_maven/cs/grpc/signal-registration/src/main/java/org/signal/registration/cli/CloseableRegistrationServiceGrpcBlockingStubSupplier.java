/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.cli;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.TlsChannelCredentials;
import io.micronaut.core.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.rpc.RegistrationServiceGrpc;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CloseableRegistrationServiceGrpcBlockingStubSupplier implements Closeable,
    Supplier<RegistrationServiceGrpc.RegistrationServiceBlockingStub> {

  private final ManagedChannel channel;
  private final RegistrationServiceGrpc.RegistrationServiceBlockingStub blockingStub;

  private static final String DEFAULT_SIGNAL_CERTIFICATE_RESOURCE_NAME = "signal.pem";

  public CloseableRegistrationServiceGrpcBlockingStubSupplier(final String host,
      final int port,
      final boolean usePlaintext,
      @Nullable final File trustedServerCertificate,
      @Nullable final String identityToken) {

    final ManagedChannelBuilder<?> managedChannelBuilder;

    if (usePlaintext) {
      managedChannelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();
    } else {
      final ChannelCredentials tlsChannelCredentials;

      if (trustedServerCertificate != null) {
        try {
          tlsChannelCredentials = TlsChannelCredentials.newBuilder()
              .trustManager(trustedServerCertificate)
              .build();
        } catch (final IOException e) {
          throw new UncheckedIOException(e);
        }
      } else {
        try (final InputStream signalCertificateInputStream = getClass().getResourceAsStream(DEFAULT_SIGNAL_CERTIFICATE_RESOURCE_NAME)) {
          if (signalCertificateInputStream == null) {
            // This should never happen for our own literally-specified certificate
            throw new AssertionError("Could not find default signal.org certificate");
          }

          tlsChannelCredentials = TlsChannelCredentials.newBuilder()
              .trustManager(signalCertificateInputStream)
              .build();
        } catch (final IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      managedChannelBuilder = Grpc.newChannelBuilderForAddress(host, port, tlsChannelCredentials);
    }

    this.channel = managedChannelBuilder.build();

    RegistrationServiceGrpc.RegistrationServiceBlockingStub stub = RegistrationServiceGrpc.newBlockingStub(channel);

    if (StringUtils.isNotBlank(identityToken)) {
      stub = stub.withCallCredentials(new IdentityTokenCallCredentials(identityToken));
    }

    this.blockingStub = stub;
  }

  @Override
  public RegistrationServiceGrpc.RegistrationServiceBlockingStub get() {
    return blockingStub;
  }

  @Override
  public void close() throws IOException {
    try {
      channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      throw new IOException("Interrupted while shutting down", e);
    }
  }
}
