/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HexFormat;
import org.signal.registration.rpc.CreateRegistrationSessionRequest;
import org.signal.registration.rpc.CreateRegistrationSessionResponse;
import picocli.CommandLine;

@CommandLine.Command(name = "create-session",
    aliases = "create",
    description = "Start a new registration session")
class CreateSession implements Runnable {

  @CommandLine.ParentCommand
  private RegistrationClient registrationClient;

  @CommandLine.Parameters(index = "0", description = "Destination phone number (e.g. 18005551234)")
  private long e164;

  @Override
  public void run() {
    try (final CloseableRegistrationServiceGrpcBlockingStubSupplier stubSupplier = registrationClient.getBlockingStubSupplier()) {
      final CreateRegistrationSessionResponse response =
          stubSupplier.get().createSession(CreateRegistrationSessionRequest.newBuilder()
              .setE164(e164)
              .build());

      switch (response.getResponseCase()) {
        case SESSION_METADATA -> {
          final String sessionIdHex = HexFormat.of().formatHex(response.getSessionMetadata().getSessionId().toByteArray());
          System.out.println("Created registration session " + sessionIdHex);
        }

        case ERROR -> {
          System.err.println("Could not create session");
          System.err.println(response);
        }

        case RESPONSE_NOT_SET -> {
          System.err.println("Response contained no data");
        }
      }

    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
