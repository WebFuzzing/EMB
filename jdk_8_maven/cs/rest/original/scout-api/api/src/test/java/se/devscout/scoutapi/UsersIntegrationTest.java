package se.devscout.scoutapi;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;
import se.devscout.scoutapi.auth.Role;
import se.devscout.scoutapi.model.User;
import se.devscout.scoutapi.resource.UserProfileView;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersIntegrationTest extends IntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<ScoutAPIConfiguration> RULE = createRule();

    @Test
    public void testProfile() throws IOException {

        Response response = createRequest("users/profile")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .get()
                ;

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        UserProfileView profile = MAPPER.readValue(response.readEntity(String.class), UserProfileView.class);

        assertThat(profile.getRole()).isEqualTo(Role.moderator.name());
    }

    @Test
    public void testUserCRUD() throws IOException {
        assertThat(getUsers()).hasSize(3);

        User createdUser = createUser("new user");
        long userId = createdUser.getId();
        assertThat(userId).isGreaterThan(0);
        assertThat(createdUser.getName()).isEqualTo("new user");
        assertThat(createdUser.getIdentities().size()).isEqualTo(1);

        assertThat(getUsers()).hasSize(4);

        User readUser = getUser(userId);
        assertThat(readUser.getName()).isEqualTo("new user");

        deleteUser(userId);

        assertThat(getUsers()).hasSize(3);
    }

    @Test
    public void testCreateUser_tooHighAuthLevel() throws IOException {
        Response response = createRequest("users")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .post(Entity.json(new User("User with too high authorization level", 99, "some api key")));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_FORBIDDEN);
    }

    @Test
    public void testUpdateUser_tooHighAuthLevel() throws IOException {
        String expected = getUser(1L).getName();

        Response response = createRequest("users/1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .put(Entity.json(new User("Don't care about the name", 99, "some api key")));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_FORBIDDEN);

        String actual = getUser(1L).getName();
        assertThat(actual).isEqualTo(expected);
    }

    private User[] getUsers() throws IOException {
        Response response = createRequest("users", Collections.singletonMap("attrs", "id,identities"))
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), User[].class);
    }

    private User getUser(long i) throws IOException {
        Response response = createRequest("users/" + i/*, Collections.singletonMap("attrs", "id")*/)
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), User.class);
    }

    private void deleteUser(long i) throws IOException {
        Response response = createRequest("users/" + i)
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .delete();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    private User createUser(String userName) throws IOException {
        Response response = createRequest("users")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .post(Entity.json(new User(userName, 0, "some api key")));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), User.class);
    }

    @Override
    protected DropwizardAppRule<ScoutAPIConfiguration> getRule() {
        return RULE;
    }
}
