package se.devscout.scoutapi;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.assertj.core.util.Strings;
import org.junit.ClassRule;
import org.junit.Test;
import se.devscout.scoutapi.model.SystemMessage;
import se.devscout.scoutapi.resource.ValidityInterval;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class SystemMessagesIntegrationTest extends IntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<ScoutAPIConfiguration> RULE = createRule();

    @Test
    public void testSystemMessageCRUD() throws IOException {

        int messagesBefore = getSystemMessages().length;

        SystemMessage createdSystemMessage = createSystemMessage("new-key", "new-value", null, null);
        long systemMessageId = createdSystemMessage.getId();
        assertThat(systemMessageId).isGreaterThan(0);
        assertThat(createdSystemMessage.getKey()).isEqualTo("new-key");
        assertThat(createdSystemMessage.getValue()).isEqualTo("new-value");

        assertThat(getSystemMessages()).hasSize(messagesBefore + 1);

        SystemMessage readSystemMessage = getSystemMessage(systemMessageId);
        assertThat(readSystemMessage.getKey()).isEqualTo("new-key");
        assertThat(readSystemMessage.getValue()).isEqualTo("new-value");

        Date validFrom = Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)));
        Date validTo = Date.from(LocalDateTime.now().plusHours(1L).toInstant(ZoneOffset.ofHours(0)));
        updateSystemMessage(systemMessageId, "new-key", "new-value", validFrom, validTo);

        SystemMessage updatedSystemMessage = getSystemMessage(systemMessageId);
        assertThat(updatedSystemMessage.getKey()).isEqualTo("new-key");
        assertThat(updatedSystemMessage.getValue()).isEqualTo("new-value");
        assertThat(updatedSystemMessage.getValidFrom()).isEqualTo(validFrom);
        assertThat(updatedSystemMessage.getValidTo()).isEqualTo(validTo);

        deleteSystemMessage(systemMessageId);

        assertThat(getSystemMessages()).hasSize(messagesBefore);
    }

    @Test
    public void testKeyFilter() throws Exception {
        Date oneHourFromNow = new Date(LocalDateTime.now().plusHours(1).toEpochSecond(ZoneOffset.ofHours(0)) * 1000);
        Date oneHourBeforeNow = new Date(LocalDateTime.now().minusHours(1).toEpochSecond(ZoneOffset.ofHours(0)) * 1000);
        Date twoHoursBeforeNow = new Date(LocalDateTime.now().minusHours(2).toEpochSecond(ZoneOffset.ofHours(0)) * 1000);

        createSystemMessage("a.a", "value", oneHourFromNow, null);
        createSystemMessage("a.a.a", "value", twoHoursBeforeNow, oneHourBeforeNow);
        createSystemMessage("a.b", "value", null, null);
        createSystemMessage("b.b", "value", null, null);

        testKeyFilter("a.", ValidityInterval.now, "a.b");
        testKeyFilter("a.", ValidityInterval.now_and_future, "a.a", "a.b");
        testKeyFilter("a.", null, "a.a.a", "a.a", "a.b");
        testKeyFilter("a.a", null, "a.a.a", "a.a");
        testKeyFilter("b", null, "b.b");
    }

    private void testKeyFilter(String key, ValidityInterval validityInterval, String... expectedKeys) throws IOException {
        List<SystemMessage> messages = Arrays.asList(getSystemMessages(key, validityInterval));
        List<String> keys = messages.stream().map(SystemMessage::getKey).collect(Collectors.toList());
        assertThat(messages).hasSize(expectedKeys.length);
        assertTrue(Stream.of(expectedKeys).allMatch(s -> keys.contains(s)));
    }

    @Test
    public void testSystemMessageAuthFail() throws Exception {
        Entity<SystemMessage> entity = Entity.json(new SystemMessage(
                "fail",
                "fail",
                Date.from(LocalDateTime.now().plusHours(1L).toInstant(ZoneOffset.UTC)), Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
        ));

        Response postResponse = createRequest("system_messages")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .post(entity);
        assertThat(postResponse.getStatus()).isEqualTo(HttpURLConnection.HTTP_FORBIDDEN);

        Response putResponse = createRequest("system_messages/1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .put(entity);
        assertThat(putResponse.getStatus()).isEqualTo(HttpURLConnection.HTTP_FORBIDDEN);

        Response deleteResponse = createRequest("system_messages/1")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_USER)
                .delete();
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpURLConnection.HTTP_FORBIDDEN);
    }

    private SystemMessage[] getSystemMessages() throws IOException {
        return getSystemMessages(null, null);
    }

    private SystemMessage[] getSystemMessages(String key, ValidityInterval validityInterval) throws IOException {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder();
        if (!Strings.isNullOrEmpty(key)) {
            builder.put("key", key);
        }
        if (validityInterval != null) {
            builder.put("valid", validityInterval.name());
        }
        Response response = createRequest("system_messages", builder.build()).get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), SystemMessage[].class);
    }

    private SystemMessage getSystemMessage(long i) throws IOException {
        Response response = createRequest("system_messages/" + i).get();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), SystemMessage.class);
    }

    private void deleteSystemMessage(long i) throws IOException {
        Response response = createRequest("system_messages/" + i)
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .delete();

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    private SystemMessage createSystemMessage(String key, String value, Date validFrom, Date validTo) throws IOException {
        Response response = createRequest("system_messages")
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .post(Entity.json(new SystemMessage(key, value, validFrom, validTo)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), SystemMessage.class);
    }

    private SystemMessage updateSystemMessage(long i, String key, String value, Date validFrom, Date validTo) throws IOException {
        Response response = createRequest("system_messages/" + i)
                .header(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_ADMINISTRATOR)
                .put(Entity.json(new SystemMessage(key, value, validFrom, validTo)));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);

        return MAPPER.readValue(response.readEntity(String.class), SystemMessage.class);
    }

    @Override
    protected DropwizardAppRule<ScoutAPIConfiguration> getRule() {
        return RULE;
    }
}
