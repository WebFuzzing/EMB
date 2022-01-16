package nl.knaw.huygens.timbuctoo.server.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.security.FederatedAuthConfiguration;
import nl.knaw.huygens.timbuctoo.security.OldStyleSecurityFactory;
import nl.knaw.huygens.timbuctoo.security.dataaccess.AccessFactory;
import nl.knaw.huygens.timbuctoo.util.TimeoutFactory;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.config.SecurityFactoryConfiguration;
import nl.knaw.huygens.timbuctoo.v5.security.SecurityFactory;
import org.apache.http.impl.client.CloseableHttpClient;


public class OldStyleSecurityFactoryConfiguration implements SecurityFactoryConfiguration {
  @JsonProperty
  private AccessFactory localAuthentication;

  @JsonProperty
  private String algorithm = "SHA-256";

  @JsonProperty
  private TimeoutFactory autoLogoutTimeout;

  @JsonProperty
  private FederatedAuthConfiguration federatedAuthentication;

  @Override
  public SecurityFactory createNewSecurityFactory(CloseableHttpClient httpCaller) {
    return new OldStyleSecurityFactory(localAuthentication, algorithm, autoLogoutTimeout, federatedAuthentication,
      new HttpCaller(httpCaller));
  }

  public AccessFactory getLocalAuthenticationForMigration() {
    return localAuthentication;
  }
}
