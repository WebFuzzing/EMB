package nl.knaw.huygens.timbuctoo.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.security.client.AuthenticationHandler;
import nl.knaw.huygens.security.client.HttpCaller;
import nl.knaw.huygens.security.client.HuygensAuthenticationHandler;
import nl.knaw.huygens.security.client.UnauthorizedException;
import nl.knaw.huygens.security.client.model.HuygensSecurityInformation;

import javax.validation.constraints.NotNull;

public class FederatedAuthConfiguration {

  @JsonProperty
  private String authenticationServerUrl;

  @JsonProperty
  private String authenticationCredentials;

  @JsonProperty
  @NotNull
  private Boolean enabled;

  public AuthenticationHandler makeHandler(HttpCaller httpCaller) {
    if (enabled) {
      if (authenticationServerUrl.equals("DUMMY")) {
        return sessionId -> {
          HuygensSecurityInformation information = new HuygensSecurityInformation();
          information.setPersistentID("33707283d426f900d4d33707283d426f900d4d0d");
          information.setDisplayName("{{Mr. Test User}}");
          return information;
        };
      } else {
        return new HuygensAuthenticationHandler(
          httpCaller,
          authenticationServerUrl,
          authenticationCredentials
        );
      }
    } else {
      return sessionId -> {
        throw new UnauthorizedException("No federated authentication configured");
      };
    }
  }
}
