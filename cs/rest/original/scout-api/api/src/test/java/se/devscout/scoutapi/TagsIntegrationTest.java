package se.devscout.scoutapi;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;
import se.devscout.scoutapi.model.Tag;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

public class TagsIntegrationTest extends IntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<ScoutAPIConfiguration> RULE = createRule();

    @Test
    public void testTagCRUD() throws IOException {

        assertThat(getTags()).hasSize(5);

        Tag createdTag = createTag("G", "N");
        long tagId = createdTag.getId();
        assertThat(tagId).isGreaterThan(0);
        assertThat(createdTag.getGroup()).isEqualTo("G");
        assertThat(createdTag.getName()).isEqualTo("N");

        assertThat(getTags()).hasSize(6);

        Tag readTag = getTag(tagId);
        assertThat(readTag.getGroup()).isEqualTo("G");
        assertThat(readTag.getName()).isEqualTo("N");

        deleteTag(tagId);

        assertThat(getTags()).hasSize(5);
    }

    @Test
    public void testActivityCounter() throws Exception {
        createActivity("fixtures/activity_properties_1.json");

        // Verify that the number of activities is 1 (activity_properties_1.json assigned two tags to the activity, one of the being the tag with id=1)
        long assignedTagId = 1L;
        long expectedNumberOfActivitiesForTag = 1L;
        Tag tag = getTag(assignedTagId);
        assertThat(tag.getActivitiesCount()).isEqualTo(expectedNumberOfActivitiesForTag);
    }

    private Tag[] getTags() throws IOException {
        Response response = createRequest("tags", "v2").get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Tag[].class);
    }

    private Tag getTag(long i) throws IOException {
        Response response = createRequest("tags/" + i, "v2").get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Tag.class);
    }

    private void deleteTag(long i) throws IOException {
        Response response = createRequest("tags/" + i, "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .delete();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    private Tag createTag(String group, String name) throws IOException {
        Response response = createRequest("tags", "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .post(Entity.json(new Tag(group, name)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Tag.class);
    }

    @Override
    protected DropwizardAppRule<ScoutAPIConfiguration> getRule() {
        return RULE;
    }
}
