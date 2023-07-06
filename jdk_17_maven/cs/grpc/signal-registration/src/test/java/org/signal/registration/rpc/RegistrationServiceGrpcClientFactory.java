/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.rpc;

import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.micronaut.grpc.server.GrpcServerChannel;

@Factory
public class RegistrationServiceGrpcClientFactory {

  @Bean
  RegistrationServiceGrpc.RegistrationServiceBlockingStub blockingStub(
      @GrpcChannel(GrpcServerChannel.NAME) final ManagedChannel channel) {

    return RegistrationServiceGrpc.newBlockingStub(channel);
  }
}
