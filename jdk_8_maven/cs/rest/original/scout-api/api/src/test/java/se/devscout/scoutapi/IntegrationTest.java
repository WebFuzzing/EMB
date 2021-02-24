package se.devscout.scoutapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.After;
import org.junit.Before;
import se.devscout.scoutapi.auth.apikey.ApiKeyAuthenticator;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class IntegrationTest {
    protected Client client;

    protected static final ObjectMapper MAPPER = Jackson.newObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

    protected static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    protected static final String HEADER_AUTHORIZATION_VALUE_USER = ApiKeyAuthenticator.ID + " user";
    protected static final String HEADER_AUTHORIZATION_VALUE_MODERATOR = ApiKeyAuthenticator.ID + " moderator";
    protected static final String HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR = ApiKeyAuthenticator.ID + " administrator";

    static DropwizardAppRule<ScoutAPIConfiguration> createRule() {
        return new DropwizardAppRule<>(ScoutAPIApplication.class, ResourceHelpers.resourceFilePath("config-integrationtest.yml"));
    }

    protected Invocation.Builder createRequest(String resource) {
        return createRequest(resource, null, "v1");
    }

    protected Invocation.Builder createRequest(String resource, final String apiVersion) {
        return createRequest(resource, null, apiVersion);
    }

    protected Invocation.Builder createRequest(String resource, Map<String, String> queryParams) {
        return createRequest(resource, queryParams, "v1");
    }

    protected Invocation.Builder createRequest(String resource, Map<String, String> queryParams, final String apiVersion) {
        WebTarget target = client.target("http://localhost:" + getRule().getLocalPort() + "/api/" + apiVersion + "/" + resource);
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                target = target.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return target.request();
    }

    protected Activity createActivity(String jsonFile) throws IOException {
        Response response = createRequest("activities", "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .post(Entity.json(MAPPER.readValue(fixture(jsonFile), ActivityProperties.class)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Activity.class);
    }

    protected abstract DropwizardAppRule<ScoutAPIConfiguration> getRule();

    @Before
    public void setUp() throws Exception {
        JerseyClientConfiguration cfg = new JerseyClientConfiguration();
        cfg.setChunkedEncodingEnabled(false);

        client = new JerseyClientBuilder(getRule().getEnvironment())
                .using(cfg)
                .build("test client " + UUID.randomUUID().toString())
                .register(MultiPartFeature.class);
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
}
