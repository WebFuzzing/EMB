package se.devscout.scoutapi;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;
import se.devscout.scoutapi.resource.SystemResource;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class SystemIntegrationTest extends IntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<ScoutAPIConfiguration> RULE = createRule();

    @Test
    public void testRoles() throws IOException {
        Response response = createRequest("system/roles").get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        final SystemResource.RolesView expected = MAPPER.readValue(fixture("fixtures/roles.json"), SystemResource.RolesView.class);
        final SystemResource.RolesView actual = MAPPER.readValue(response.readEntity(String.class), SystemResource.RolesView.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testPing() throws IOException {
        Response response = createRequest("system/ping").get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }


    @Override
    protected DropwizardAppRule<ScoutAPIConfiguration> getRule() {
        return RULE;
    }
}
