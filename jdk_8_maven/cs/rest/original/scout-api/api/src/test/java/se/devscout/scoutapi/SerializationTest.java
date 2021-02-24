package se.devscout.scoutapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import se.devscout.scoutapi.model.Tag;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES).setFilterProvider(ScoutAPIApplication.DEFAULT_FILTER_PROVIDER);

    @Test
    public void serializesToJSON() throws Exception {
        final Tag tag = new Tag("The Group", "The Name");

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/tag.json"), Tag.class));

        assertThat(MAPPER.writeValueAsString(tag)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final Tag expected = new Tag("The Group", "The Name");

        assertThat(MAPPER.readValue(fixture("fixtures/tag.json"), Tag.class)).isEqualTo(expected);
    }
}
