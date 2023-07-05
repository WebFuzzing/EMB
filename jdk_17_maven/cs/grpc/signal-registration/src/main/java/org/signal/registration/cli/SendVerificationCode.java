/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.cli;

import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.signal.registration.rpc.SendVerificationCodeRequest;
import org.signal.registration.rpc.SendVerificationCodeResponse;
import org.signal.registration.sender.ClientType;
import org.signal.registration.sender.MessageTransport;
import org.signal.registration.util.ClientTypes;
import picocli.CommandLine;

@CommandLine.Command(name = "send-verification-code",
    aliases = "send",
    description = "Send a verification code to a phone number associated with a session")
class SendVerificationCode implements Runnable {

  @CommandLine.ParentCommand
  private RegistrationClient registrationClient;

  @CommandLine.Parameters(index = "0", description = "Hex-formatted registration session ID")
  private String sessionId;

  @CommandLine.Option(names = {"--transport"},
      description = "Message transport (one of ${COMPLETION-CANDIDATES}; default ${DEFAULT-VALUE})",
      defaultValue = "SMS")
  private MessageTransport messageTransport;

  @CommandLine.Option(names = {"--client-type"},
      description = "Client type (one of ${COMPLETION-CANDIDATES}; default ${DEFAULT-VALUE})",
      defaultValue = "UNKNOWN")
  private ClientType clientType;

  @CommandLine.Option(names = {"--accept-language"},
      description = "Accepted languages (value of an Accept-Language header)")
  private String acceptLanguage;

  @CommandLine.Option(names = {"--sender-name"},
      description = "Always use this sender"
  )
  private String senderName;

  @Override
  public void run() {
    try (final CloseableRegistrationServiceGrpcBlockingStubSupplier stubSupplier = registrationClient.getBlockingStubSupplier()) {
      final org.signal.registration.rpc.MessageTransport rpcTransport = switch (messageTransport) {
        case SMS -> org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS;
        case VOICE -> org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_VOICE;
      };

      final org.signal.registration.rpc.ClientType rpcClientType =
          ClientTypes.getRpcClientTypeFromSenderClientType(clientType);

      final SendVerificationCodeRequest.Builder requestBuilder = SendVerificationCodeRequest.newBuilder()
          .setSessionId(ByteString.copyFrom(Hex.decodeHex(sessionId)))
          .setTransport(rpcTransport)
          .setClientType(rpcClientType);

      if (acceptLanguage != null) {
        requestBuilder.setAcceptLanguage(acceptLanguage);
      }

      if (senderName != null) {
        requestBuilder.setSenderName(senderName);
      }

      final SendVerificationCodeResponse response = stubSupplier.get().sendVerificationCode(requestBuilder.build());

      if (response.hasError()) {
        System.err.println("Could not send verification code");
        System.err.println(response);
      } else {
        System.out.println("Sent verification code");
        System.out.println(response);
      }

      System.out.println("Sent verification code");
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (DecoderException e) {
      throw new IllegalArgumentException("Could not decode session ID as a hexadecimal value", e);
    }
  }
}
