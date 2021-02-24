package se.devscout.scoutapi;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.MediaFile;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class MediaFilesIntegrationTest extends IntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<ScoutAPIConfiguration> RULE = createRule();
    private static final MediaType IMAGE_JPEG_MEDIA_TYPE = new MediaType("image", "jpeg");

    @Override
    @After
    public void tearDown() throws Exception {
        Files.list(RULE.getConfiguration().getMediaFilesFolder().toPath()).forEach(path -> path.toFile().deleteOnExit());
    }

    @Test
    public void testMediaFileUpload() throws Exception {
        StreamDataBodyPart bodyPartWithInvalidImage = new StreamDataBodyPart(
                UUID.randomUUID().toString(),
                Resources.getResource("fixtures/roles.json").openStream(),
                "roles.json",
                IMAGE_JPEG_MEDIA_TYPE);
        StreamDataBodyPart bodyPartWithImage = new StreamDataBodyPart(
                UUID.randomUUID().toString(),
                Resources.getResource("cat.jpg").openStream(),
                "cat.jpg",
                IMAGE_JPEG_MEDIA_TYPE);
        MultiPart multipart = new FormDataMultiPart()
                .bodyPart(bodyPartWithImage)
                .bodyPart(bodyPartWithInvalidImage);

        long persistedFilesBefore = Files.list(RULE.getConfiguration().getMediaFilesFolder().toPath()).count();

        Response response = createRequest("media_files")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .post(Entity.entity(multipart, multipart.getMediaType()));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        long persistedFilesAfter = Files.list(RULE.getConfiguration().getMediaFilesFolder().toPath()).count();
        assertThat(persistedFilesAfter).isEqualTo(persistedFilesBefore + 1 /* <--- Only one of the two files should be saved to disk. */);

        MediaFile[] mediaFiles = MAPPER.readValue(response.readEntity(String.class), MediaFile[].class);
        assertThat(mediaFiles.length).isEqualTo(1);
        assertThat(mediaFiles[0].getName()).isEqualTo("cat.jpg");
    }

    @Test
    public void testMediaFileCRUD() throws IOException {

        int mediaFilesBefore = getMediaFiles().length;

        MediaFile createdMediaFile = createMediaFile("Alice's Profile Picture", "image/jpeg", "http://example.com/profile-alice.jpg");
        long mediaFileId = createdMediaFile.getId();
        assertThat(mediaFileId).isGreaterThan(0);
        assertThat(createdMediaFile.getName()).isEqualTo("Alice's Profile Picture");
        assertThat(createdMediaFile.getMimeType()).isEqualTo("image/jpeg");
        assertThat(createdMediaFile.getUri()).isEqualTo("http://example.com/profile-alice.jpg");

        assertThat(getMediaFiles()).hasSize(mediaFilesBefore + 1);

        MediaFile readMediaFile = getMediaFile(mediaFileId);
        assertThat(readMediaFile.getName()).isEqualTo("Alice's Profile Picture");
        assertThat(readMediaFile.getMimeType()).isEqualTo("image/jpeg");
        assertThat(readMediaFile.getUri()).isEqualTo("http://example.com/profile-alice.jpg");

        updateMediaFile(mediaFileId, "Bob's Profile Picture", "image/gif", "http://example.com/profile-bob.gif");

        MediaFile updatedMediaFile = getMediaFile(mediaFileId);
        assertThat(updatedMediaFile.getName()).isEqualTo("Bob's Profile Picture");
        assertThat(updatedMediaFile.getMimeType()).isEqualTo("image/gif");
        assertThat(updatedMediaFile.getUri()).isEqualTo("http://example.com/profile-bob.gif");

        deleteMediaFile(mediaFileId, null);

        assertThat(getMediaFiles()).hasSize(mediaFilesBefore);
    }

    @Test
    public void testVerifyUsed() throws Exception {
        Activity activity = createActivity("fixtures/activity_properties_3.json");

        long mediaFileId = activity.getProperties().getMediaFiles().get(0).getId();

        deleteMediaFile(mediaFileId, Boolean.TRUE, 409);
        deleteMediaFile(mediaFileId, null, 204);
    }

    protected Activity createActivity(String jsonFile) throws IOException {
        Response response = createRequest("activities", "v2")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .post(Entity.json(MAPPER.readValue(fixture(jsonFile), ActivityProperties.class)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), Activity.class);
    }

    @Test
    public void testUploadAndDownload() throws IOException, URISyntaxException {
        byte[] expected = Resources.toByteArray(Resources.getResource("config-integrationtest.yml"));
        String data = Base64.getEncoder().encodeToString(expected);

        MediaFile createdMediaFile = createMediaFile("config-integrationtest.yml", "text/xml", "data:application/xml;base64," + data);

        Response respGet = createRequest("media_files/" + createdMediaFile.getId() + "/file"/*, Collections.singletonMap("size", "300")*/).get();
        assertThat(respGet.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        byte[] actual = ByteStreams.toByteArray((InputStream) respGet.getEntity());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testUploadAndDownloadResizedImage() throws IOException, URISyntaxException {
        byte[] expected = Resources.toByteArray(Resources.getResource("scouterna-logo.jpg"));
        String data = Base64.getEncoder().encodeToString(expected);

        MediaFile createdMediaFile = createMediaFile("scouterna-logo.jpg", "image/jpeg", "data:image/jpeg;base64," + data);

        Response respGet = createRequest("media_files/" + createdMediaFile.getId() + "/file", Collections.singletonMap("size", "300")).get();
        assertThat(respGet.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(respGet.getMediaType().getType()).isEqualTo("image");
        byte[] actual = ByteStreams.toByteArray((InputStream) respGet.getEntity());

        assertThat(actual.length).isGreaterThan(0);
        assertThat(actual.length).isLessThan(expected.length);
    }

    private MediaFile[] getMediaFiles() throws IOException {
        Response response = createRequest("media_files").get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), MediaFile[].class);
    }

    private MediaFile getMediaFile(long i) throws IOException {
        Response response = createRequest("media_files/" + i).get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), MediaFile.class);
    }

    private void deleteMediaFile(long i, Boolean verifyUnused) throws IOException {
        deleteMediaFile(i, verifyUnused, HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void deleteMediaFile(long i, Boolean verifyUnused, int expectedResponseCode) throws IOException {
        Response response = createRequest("media_files/" + i + (verifyUnused != null ? "?verify_unused=" + verifyUnused : ""))
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .delete();

        assertThat(response.getStatus()).isEqualTo(expectedResponseCode);
    }

    private MediaFile createMediaFile(String name, String mimeType, String uri) throws IOException {
        Response response = createRequest("media_files")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .post(Entity.json(new MediaFile(mimeType, uri, name)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), MediaFile.class);
    }

    private MediaFile updateMediaFile(long i, String name, String mimeType, String uri) throws IOException {
        Response response = createRequest("media_files/" + i)
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_MODERATOR)
                .put(Entity.json(new MediaFile(mimeType, uri, name)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), MediaFile.class);
    }

    @Override
    protected DropwizardAppRule<ScoutAPIConfiguration> getRule() {
        return RULE;
    }
}
