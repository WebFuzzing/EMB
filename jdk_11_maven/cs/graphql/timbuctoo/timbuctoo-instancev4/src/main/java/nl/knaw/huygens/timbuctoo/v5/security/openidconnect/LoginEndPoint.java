package nl.knaw.huygens.timbuctoo.v5.security.openidconnect;


import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Path("/v5/openid-connect")
public class LoginEndPoint {
  public static final Logger LOG = LoggerFactory.getLogger(LoginEndPoint.class);
  private final Map<UUID, String> loginSessionRedirects;
  private final OpenIdClient openIdClient;

  public LoginEndPoint(OpenIdClient openIdClient) {
    this.openIdClient = openIdClient;
    loginSessionRedirects = new HashMap<>();
  }


  @GET
  @Path("/login")
  public Response login(@QueryParam("redirect-uri") String clientRedirectUri) {
    LOG.info("login");
    if (StringUtils.isBlank(clientRedirectUri)) {
      return Response.status(400).entity("expected a query param redirect-uri").build();
    }

    UUID sessionId = UUID.randomUUID();
    loginSessionRedirects.put(sessionId, clientRedirectUri);

    try {
      return openIdClient.createRedirectResponse(sessionId);
    } catch (IOException | ParseException e) {
      LOG.error("Could not create redirect to OpenID Connect server", e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/callback")
  public Response callback(@QueryParam("state") UUID loginSession, @QueryParam("code") String code) {
    if (!loginSessionRedirects.containsKey(loginSession)) {
      return Response.status(417).entity("Login session unknown").build();
    }

    try {
      final Optional<Tokens> userTokens = openIdClient.getUserTokens(code);
      final String value = userTokens.isPresent() ? userTokens.get().getBearerAccessToken().getValue() : "no-token";
      final URI userUri = UriBuilder.fromUri(loginSessionRedirects.get(loginSession))
                                    .queryParam("sessionToken", value)
                                    .build();
      return Response.temporaryRedirect(userUri).build();

    } catch (IOException | ParseException e) {
      LOG.error("Retrieval of userTokes failed", e);
      return Response.serverError().build();
    }
  }
}
