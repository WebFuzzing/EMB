package nl.knaw.huygens.timbuctoo.server;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.timbuctoo.util.EvilEnvironmentVariableHacker;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;

public class DropwizardLaunchesTest {

  static {
    EvilEnvironmentVariableHacker.setEnv(
      "http://localhost",
      "9200",
      "elastic",
      "changeme",
      "http://127.0.0.1:0",
      resourceFilePath("testrunstate"),
      resourceFilePath("testrunstate"),
      "0",
      "0"
    );
  }

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APP = new DropwizardAppRule<>(
    TimbuctooV4.class,
    "example_config.yaml",
    ConfigOverride.config(
        "collectionFilters.elasticsearch.@class",
        "nl.knaw.huygens.timbuctoo.server.TestCollectionFilter"
    )
  );


  @Test
  public void dropwizardLaunches() throws Exception {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("http://localhost:%d/healthcheck", APP.getAdminPort()));

    Response response = target.request().get();

    assertThat(response.getStatus()).isEqualTo(200);
  }
}
