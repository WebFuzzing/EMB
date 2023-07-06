/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.cli;

import com.google.protobuf.ByteString;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.signal.registration.rpc.GetRegistrationSessionMetadataRequest;
import org.signal.registration.rpc.GetRegistrationSessionMetadataResponse;
import picocli.CommandLine;
import java.io.IOException;
import java.io.UncheckedIOException;

@CommandLine.Command(name = "get-session",
    aliases = "get",
    description = "Describe an existing registration session")
class GetSession implements Runnable {

  @CommandLine.ParentCommand
  private RegistrationClient registrationClient;

  @CommandLine.Parameters(index = "0", description = "Hex-formatted registration session ID")
  private String sessionId;

  @Override
  public void run() {
    try (final CloseableRegistrationServiceGrpcBlockingStubSupplier stubSupplier = registrationClient.getBlockingStubSupplier()) {
      final GetRegistrationSessionMetadataResponse response =
          stubSupplier.get().getSessionMetadata(GetRegistrationSessionMetadataRequest.newBuilder()
              .setSessionId(ByteString.copyFrom(Hex.decodeHex(sessionId)))
              .build());

      switch (response.getResponseCase()) {
        case SESSION_METADATA -> System.out.println(response.getSessionMetadata());
        case ERROR -> System.err.println("Could not get session: " + response.getError().getErrorType());
        case RESPONSE_NOT_SET -> throw new RuntimeException("Response contained no data");
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (DecoderException e) {
      throw new IllegalArgumentException("Could not decode session ID as a hexadecimal value", e);
    }
  }
}
