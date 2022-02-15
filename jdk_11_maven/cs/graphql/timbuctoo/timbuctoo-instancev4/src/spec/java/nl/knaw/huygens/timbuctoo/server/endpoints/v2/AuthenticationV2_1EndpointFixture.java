package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.HttpRequest;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static nl.knaw.huygens.util.DropwizardMaker.makeTimbuctoo;

@FullOGNL
@RunWith(ConcordionRunner.class)
public class AuthenticationV2_1EndpointFixture extends AbstractV2_1EndpointFixture {

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APPLICATION = makeTimbuctoo();

  public String doLoginWithInvalidUsernameAndPassword() {
    HttpRequest httpRequest = new HttpRequest("POST", "/v2.1/authenticate")
      // Authorization header for unknownUser:password
      .withHeader("Authorization", "Basic dW5rbm93blVzZXI6cGFzc3dvcmQ=");

    ActualResult response = super.executeRequestUsingJaxRs(httpRequest);

    return String.format("%s %s", response.getStatus(), response.getStatusInfo());
  }

  public String doLoginWithoutHeader() {
    HttpRequest httpRequest = new HttpRequest("POST", "/v2.1/authenticate");

    ActualResult response = super.executeRequestUsingJaxRs(httpRequest);

    return String.format("%s %s", response.getStatus(), response.getStatusInfo());
  }

  public boolean hasContent(String value) {
    return !StringUtils.isBlank(value);
  }

  @Override
  protected WebTarget returnUrlToMockedOrRealServer(String serverAddress) {
    String defaultAddress = String.format("http://localhost:%d", APPLICATION.getLocalPort());
    String address = serverAddress != null ? serverAddress : defaultAddress;

    return ClientBuilder.newClient().target(address);
  }

}
