package nl.knaw.huygens.timbuctoo.v5.security.openidconnect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static javax.ws.rs.core.UriBuilder.fromUri;

public class OpenIdClient {
  public static final Logger LOG = LoggerFactory.getLogger(OpenIdClient.class);
  private final URI redirectUrl;
  private final String discoveryUrl;
  private final String clientId;
  private final String clientSecret;
  private final String scope;

  @JsonCreator
  public OpenIdClient(@JsonProperty("discoveryUrl") String discoveryUrl,
                      @JsonProperty("clientId") String clientId,
                      @JsonProperty("clientSecret") String clientSecret,
                      @JsonProperty("scope") String scope,
                      @JsonProperty("baseUri") String baseUri,
                      @JsonProperty("port") int port
  ) {
    this.discoveryUrl = discoveryUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.scope = scope;
    this.redirectUrl = fromUri(baseUri).port(port).path("v5").path("openid-connect").path("callback").build();
  }

  public Optional<UserInfo> getUserInfo(String accessToken) throws IOException, ParseException {
    final URI userInfoUri = fromUri(getUserInfUrl(discoveryUrl)).build();
    final UserInfoRequest userInfoRequest = new UserInfoRequest(userInfoUri, new BearerAccessToken(accessToken));
    final UserInfoResponse userInfoResponse = UserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());

    if (userInfoResponse.indicatesSuccess()) {
      return Optional.of(userInfoResponse.toSuccessResponse().getUserInfo());
    } else {
      LOG.warn("User info request failed: {}", userInfoResponse.toErrorResponse().getErrorObject());
      return Optional.empty();
    }
  }

  private URI getUserInfUrl(String discoveryUrl) throws IOException, ParseException {
    final OIDCProviderConfigurationRequest configurationRequest =
        new OIDCProviderConfigurationRequest(new Issuer(discoveryUrl));
    final OIDCProviderMetadata metadata =
        OIDCProviderMetadata.parse(configurationRequest.toHTTPRequest().send().getContentAsJSONObject());

    return metadata.getUserInfoEndpointURI();
  }

  private URI getAuthorizationUrl(String discoveryUrl) throws IOException, ParseException {
    final OIDCProviderConfigurationRequest configurationRequest =
        new OIDCProviderConfigurationRequest(new Issuer(discoveryUrl));
    final OIDCProviderMetadata metadata =
        OIDCProviderMetadata.parse(configurationRequest.toHTTPRequest().send().getContentAsJSONObject());

    return metadata.getAuthorizationEndpointURI();
  }

  private URI getTokenUrl(String discoveryUrl) throws IOException, ParseException {
    final OIDCProviderConfigurationRequest configurationRequest =
        new OIDCProviderConfigurationRequest(new Issuer(discoveryUrl));
    final OIDCProviderMetadata metadata =
        OIDCProviderMetadata.parse(configurationRequest.toHTTPRequest().send().getContentAsJSONObject());

    return metadata.getTokenEndpointURI();
  }

  public Response createRedirectResponse(UUID sessionId) throws IOException, ParseException {
    final URI openIdServer = fromUri(getAuthorizationUrl(discoveryUrl))
                                       .queryParam("response_type", "code")
                                       .queryParam("client_id", clientId)
                                       .queryParam("redirect_uri", redirectUrl)
                                       .queryParam("scope", scope)
                                       .queryParam("state", sessionId)
                                       .build();

    return Response.status(308).location(openIdServer).build();
  }

  public Optional<Tokens> getUserTokens(String code) throws IOException, ParseException {
    final ClientAuthentication basicAuth = new ClientSecretBasic(new ClientID(clientId), new Secret(clientSecret));
    final URI redirectUri = fromUri(redirectUrl).build();
    final AuthorizationCodeGrant authzGrant = new AuthorizationCodeGrant(new AuthorizationCode(code), redirectUri);
    final TokenRequest tokenRequest = new TokenRequest(getTokenUrl(discoveryUrl), basicAuth, authzGrant);
    final TokenResponse response = OIDCTokenResponseParser.parse(tokenRequest.toHTTPRequest().send());

    if (response.indicatesSuccess()) {
      final Tokens tokens = response.toSuccessResponse().getTokens();

      // TODO check if the id is not fake
      return Optional.of(tokens);
    } else {
      LOG.error("Could not retrieve client token: {}", response.toErrorResponse().getErrorObject());
      return Optional.empty();
    }
  }
}
