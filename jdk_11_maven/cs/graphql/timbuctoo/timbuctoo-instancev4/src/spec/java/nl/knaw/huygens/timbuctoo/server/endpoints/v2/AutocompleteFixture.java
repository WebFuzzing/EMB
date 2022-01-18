package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;
import java.net.URISyntaxException;

import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.assertThat;
import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.jsonDiffer;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.util.DropwizardMaker.makeTimbuctoo;

@RunWith(ConcordionRunner.class)
public class AutocompleteFixture extends AbstractV2_1EndpointFixture {

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APPLICATION = makeTimbuctoo();

  @Override
  protected WebTarget returnUrlToMockedOrRealServer(String serverAddress) {
    String defaultAddress = String.format("http://localhost:%d", APPLICATION.getLocalPort());
    String address = serverAddress != null ? serverAddress : defaultAddress;

    return ClientBuilder.newClient().target(address);
  }

  @Override
  protected JsonDiffer makeJsonDiffer() {
    return jsonDiffer()
      .handleArraysWith("ALL_MATCH", node -> jsnO("expected", node.get(0)))
      .withCustomHandler("URL_WITHOUT_REV", node -> {
        try {
          URI uri = new URI(node.asText());
          if (uri.isAbsolute()) {
            if (uri.getQuery() != null && uri.getQuery().contains("rev=")) {
              return assertThat(false, "should not have a rev parameter", node);
            } else {
              return assertThat(true, "a valid URI, without a rev query parameter", node);
            }
          } else {
            return assertThat(false, "Scheme component is missing", node);
          }
        } catch (URISyntaxException e) {
          return assertThat(false, "this does not parse as a URI)", node);
        }
      })
      .build();
  }

}
