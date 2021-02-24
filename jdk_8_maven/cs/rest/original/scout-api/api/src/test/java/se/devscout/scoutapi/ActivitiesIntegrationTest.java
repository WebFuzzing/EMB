package se.devscout.scoutapi;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.ActivityRatingAttrs;
import se.devscout.scoutapi.resource.v1.FavouritesResourceV1;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class ActivitiesIntegrationTest extends IntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<ScoutAPIConfiguration> RULE = createRule();

    @Test
    public void testActivityCRUD() throws IOException {
        assertThat(getActivities()).hasSize(0);

        Activity createdActivity = createActivity("fixtures/activity_properties_1.json");
        long activityId = createdActivity.getId();
        assertThat(activityId).isGreaterThan(0);
        assertThat(createdActivity.getProperties().getName()).isEqualTo("A New Activity");
        assertThat(createdActivity.getProperties().getDescriptionMaterial()).isEqualTo("Materials");
        assertThat(createdActivity.getProperties().getAgeMin()).isEqualTo(5);
        assertThat(createdActivity.getProperties().getTags()).hasSize(2);
        assertThat(createdActivity.getProperties().getMediaFiles()).hasSize(2);

        assertThat(getActivities()).hasSize(1);

        Activity readActivity = getActivity(activityId);
        assertThat(readActivity.getProperties().getName()).isEqualTo("A New Activity");

        updateActivity(activityId, "fixtures/activity_properties_2.json", true);

        Activity patchedActivity = getActivity(activityId);
        assertThat(patchedActivity.getProperties().getName()).isEqualTo("A Patched Activity");
        assertThat(patchedActivity.getProperties().getDescriptionMaterial()).isEqualTo("Materials");
        assertThat(createdActivity.getProperties().getAgeMin()).isEqualTo(5);
        assertThat(patchedActivity.getProperties().getTags()).hasSize(2);
        assertThat(patchedActivity.getProperties().getMediaFiles()).hasSize(2);

        updateActivity(activityId, "fixtures/activity_properties_3.json", false);

        Activity updatedActivity = getActivity(activityId);
        assertThat(updatedActivity.getProperties().getName()).isEqualTo("An Updated Activity");
        assertThat(updatedActivity.getProperties().getDescriptionMaterial()).isEqualTo("Materials");
        assertThat(updatedActivity.getProperties().getDescriptionIntroduction()).isEqualTo("The Introduction Has Been Updated");
        assertThat(createdActivity.getProperties().getAgeMin()).isEqualTo(5);
        assertThat(updatedActivity.getProperties().getTags()).hasSize(1);
        assertThat(updatedActivity.getProperties().getMediaFiles()).hasSize(1);

        deleteActivity(activityId);

        assertThat(getActivities()).hasSize(0);
    }

    @Test
    public void testRatings() throws Exception {
        Activity createdActivity = createActivity("fixtures/activity_properties_1.json");
        long activityId = createdActivity.getId();

        // Get ratings for activity and verify that no ratings exist.
        checkActivityRatings(-1.0, 0, 0, activityId, 0);

        // Set rating for user 1
        setActivityRating(activityId, 5, false, HEADER_AUTHORIZATION_VALUE_USER);

        // Set rating and favourite for user 2
        setActivityRating(activityId, 4, true, HEADER_AUTHORIZATION_VALUE_MODERATOR);

        // Get activity and verify number of ratings and average rating.
        checkActivityRatings(4.5, 2, 9, activityId, 1);

        // Update rating for user 1
        setActivityRating(activityId, 3, false, HEADER_AUTHORIZATION_VALUE_USER);

        // Get activity and verify number of ratings and average rating.
        checkActivityRatings(3.5, 2, 7, activityId, 1);

        // Delete rating for user 2 (remove rating for activity but keep as favourite)
        unsetActivityRating(activityId, HEADER_AUTHORIZATION_VALUE_MODERATOR);

        // Get activity and verify number of ratings and average rating.
        checkActivityRatings(3.0, 1, 3, activityId, 1);
    }

    @Test
    public void testApiV1SetFavourites() throws Exception {
        long a1Id = createActivity("fixtures/activity_properties_1.json").getId();
        long a2Id = createActivity("fixtures/activity_properties_1.json").getId();
        long a3Id = createActivity("fixtures/activity_properties_1.json").getId();

        // Set some ratings initial ratings, and no favourites.
        setActivityRating(a1Id, 3, false, HEADER_AUTHORIZATION_VALUE_USER);
        setActivityRating(a1Id, 4, false, HEADER_AUTHORIZATION_VALUE_MODERATOR);

        checkActivityRatings(3.5, 2, 7, a1Id, 0);

        // Set activity 1 and 2 as favourites of user User. This must not affect the current rating(s) of activity 1.
        Response responseUser = createRequest("favourites", "v1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .post(Entity.json(new FavouritesResourceV1.PutFavouritesEntity(Arrays.asList(a1Id, a2Id))));
        assertThat(responseUser.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);

        // Set activity 2 and 3 as favourites of user Moderator. No ratings should be affected, since only activity 1 has been rated.
        Response responseModerator = createRequest("favourites", "v1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .post(Entity.json(new FavouritesResourceV1.PutFavouritesEntity(Arrays.asList(a2Id, a3Id))));
        assertThat(responseModerator.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);

        // Verify that activity 1 has kepts its ratings while gaining a favourite mark from user User.
        checkActivityRatings(3.5, 2, 7, a1Id, 1);
        // Verify that activity 2 and 3 still has no ratings but that they have gained some favourite markers.
        checkActivityRatings(-1.0, 0, 0, a2Id, 2);
        checkActivityRatings(-1.0, 0, 0, a3Id, 1);

        // Set activity 1 and 3 as favourites of user User. This means activity 2 is no longer a favourite of user User and that activity 3 is now favoured by one more user.
        Response responseUser2 = createRequest("favourites", "v1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .post(Entity.json(new FavouritesResourceV1.PutFavouritesEntity(Arrays.asList(a1Id, a3Id))));
        assertThat(responseUser2.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);

        // Verify that nothing has changed for activity one.
        checkActivityRatings(3.5, 2, 7, a1Id, 1);

        // Verify that activity 2 has lost one and activity 3 has gained one favourite marking.
        checkActivityRatings(-1.0, 0, 0, a2Id, 1);
        checkActivityRatings(-1.0, 0, 0, a3Id, 2);

        Response responseUserFavs = createRequest("favourites", "v1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .get();
        assertThat(responseUserFavs.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        List<Integer> favourites = MAPPER.readValue(responseUserFavs.readEntity(String.class), List.class);
        assertThat(favourites).hasSize(2);
        assertTrue(Stream.of(a1Id, a3Id).allMatch(id -> favourites.contains(Long.valueOf(id).intValue())));
    }

    private void setActivityRating(long activityId, int rating, boolean favourite, String user) {
        Response response = createRequest("activities/" + activityId + "/rating", "v2")
                .header(HEADER_AUTHORIZATION_KEY, user)
                .post(Entity.json(new ActivityRatingAttrs(rating, favourite)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void unsetActivityRating(long activityId, String user) {
        Response response = createRequest("activities/" + activityId + "/rating", "v2")
                .header(HEADER_AUTHORIZATION_KEY, user)
                .delete();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void checkActivityRatings(double ratingsAverage, long ratingsCount, long ratingsSum, long activityId, long favouritesCount) throws IOException {
        Response response = createRequest("activities/" + activityId, "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        Activity activity = MAPPER.readValue(response.readEntity(String.class), Activity.class);

        assertThat(activity.getFavouritesCount()).isEqualTo(favouritesCount);
        assertThat(activity.getRatingsAverage()).isEqualTo(ratingsAverage);
        assertThat(activity.getRatingsCount()).isEqualTo(ratingsCount);
        assertThat(activity.getRatingsSum()).isEqualTo(ratingsSum);
    }

    private Activity[] getActivities() throws IOException {
        Response response = createRequest("activities"/*, Collections.singletonMap("attrs", "id,identities")*/, "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Activity[].class);
    }

    private Activity getActivity(long activityId) throws IOException {
        Response response = createRequest("activities/" + activityId, "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Activity.class);
    }

    private void deleteActivity(long activityId) throws IOException {
        Response response = createRequest("activities/" + activityId, "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .delete();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void updateActivity(long activityId, String jsonFile, boolean patch) throws IOException {
        Response response = createRequest("activities/" + activityId, "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .method(patch ? "PATCH" : "PUT", Entity.json(MAPPER.readValue(fixture(jsonFile), ActivityProperties.class)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
    }

    @Override
    protected DropwizardAppRule<ScoutAPIConfiguration> getRule() {
        return RULE;
    }
}
