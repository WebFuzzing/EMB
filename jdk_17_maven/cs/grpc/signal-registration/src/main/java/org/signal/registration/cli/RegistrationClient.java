/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.cli;

import picocli.CommandLine;
import java.io.File;

@CommandLine.Command(subcommands = {
    CreateSession.class,
    GetSession.class,
    SendVerificationCode.class,
    CheckVerificationCode.class
})
public class RegistrationClient {

  @CommandLine.Option(names = {"--host"},
      description = "Registration service hostname (default: ${DEFAULT-VALUE})",
      defaultValue = "localhost",
      scope = CommandLine.ScopeType.INHERIT)
  private String host;

  @CommandLine.Option(names = {"--port"},
      description = "Registration service port (default: ${DEFAULT-VALUE})",
      defaultValue = "50051",
      scope = CommandLine.ScopeType.INHERIT)
  private int port;

  @CommandLine.Option(names = {"--identity-token"},
      description = "OIDC identity token for this call",
      scope = CommandLine.ScopeType.INHERIT)
  private String identityToken;

  @CommandLine.ArgGroup
  private TlsOptions tlsOptions;

  static class TlsOptions {
    @CommandLine.Option(names = {"--plaintext"},
        description = "Use plaintext instead of TLS? (default: ${DEFAULT-VALUE})",
        defaultValue = "false",
        scope = CommandLine.ScopeType.INHERIT)
    private boolean usePlaintext;

    @CommandLine.Option(names = {"--trusted-server-certificate"},
        description = "Path to a trusted server certificate; signal.org certificate trusted by default",
        scope = CommandLine.ScopeType.INHERIT)
    private File trustedServerCertificate;
  }

  public CloseableRegistrationServiceGrpcBlockingStubSupplier getBlockingStubSupplier() {
    final boolean usePlaintext = tlsOptions != null && tlsOptions.usePlaintext;
    final File trustedServerCertificate = tlsOptions != null ? tlsOptions.trustedServerCertificate : null;

    return new CloseableRegistrationServiceGrpcBlockingStubSupplier(host, port, usePlaintext, trustedServerCertificate,
        identityToken);
  }

  public static void main(final String... args) {
    new CommandLine(new RegistrationClient()).execute(args);
  }
}
