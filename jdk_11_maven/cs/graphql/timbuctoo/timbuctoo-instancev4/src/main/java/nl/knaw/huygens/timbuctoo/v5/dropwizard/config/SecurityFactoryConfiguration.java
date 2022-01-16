package nl.knaw.huygens.timbuctoo.v5.dropwizard.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.timbuctoo.v5.security.SecurityFactory;
import org.apache.http.impl.client.CloseableHttpClient;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface SecurityFactoryConfiguration {
  SecurityFactory createNewSecurityFactory(CloseableHttpClient httpCaller);
}
