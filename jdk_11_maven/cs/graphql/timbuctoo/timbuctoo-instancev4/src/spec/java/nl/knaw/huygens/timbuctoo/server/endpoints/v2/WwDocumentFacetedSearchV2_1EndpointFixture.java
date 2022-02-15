package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static nl.knaw.huygens.util.DropwizardMaker.makeTimbuctoo;

@FullOGNL
@RunWith(ConcordionRunner.class)
public class WwDocumentFacetedSearchV2_1EndpointFixture extends AbstractV2_1EndpointFixture {

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APPLICATION = makeTimbuctoo();

  public String isFullyQualified(String url) {
    if (StringUtils.isBlank(url) || (!url.startsWith("http://") && !url.startsWith("https://"))) {
      return "not a fully qualified HTTP url";
    } else {
      return "a fully qualified HTTP url";
    }
  }

  public String doRequestWithMalformedUuid() {
    Response response = returnUrlToMockedOrRealServer(null)
        .path("v2.1").path("search").path("malformedUUID")
        .request()
        .get();

    return String.format("%s %s", response.getStatus(), response.getStatusInfo());
  }

  public String doGetOfUnknown() {
    Response response = returnUrlToMockedOrRealServer(null)
        .path("v2.1").path("search").path("058f1746-c115-4cbe-9b57-cebd105046eb")
        .request()
        .get();

    return String.format("%s %s", response.getStatus(), response.getStatusInfo());
  }

  @Override
  protected WebTarget returnUrlToMockedOrRealServer(String serverAddress) {
    String defaultAddress = String.format("http://localhost:%d", APPLICATION.getLocalPort());
    String address = serverAddress != null ? serverAddress : defaultAddress;

    return ClientBuilder.newClient().target(address);
  }
}
