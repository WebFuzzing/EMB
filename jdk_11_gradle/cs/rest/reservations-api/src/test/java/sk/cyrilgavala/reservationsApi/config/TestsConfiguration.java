package sk.cyrilgavala.reservationsApi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({WebConfiguration.class, SecurityConfiguration.class})
public class TestsConfiguration {
}
