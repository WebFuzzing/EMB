package nl.knaw.huygens.timbuctoo.v5.security.twitterexample;

import nl.knaw.huygens.timbuctoo.v5.dropwizard.config.SecurityFactoryConfiguration;
import nl.knaw.huygens.timbuctoo.v5.security.SecurityFactory;
import org.apache.http.impl.client.CloseableHttpClient;

public class TwitterSecurityFactoryConfiguration implements SecurityFactoryConfiguration {
  @Override
  public SecurityFactory createNewSecurityFactory(CloseableHttpClient httpCaller) {
    return new TwitterSecurityFactory();
  }
}
