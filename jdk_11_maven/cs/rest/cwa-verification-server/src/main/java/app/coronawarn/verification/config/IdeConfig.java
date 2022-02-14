package app.coronawarn.verification.config;

import java.util.Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Needed to able to run from an IDE, otherwise OpenApiConfig crashes.
 */
@Configuration
public class IdeConfig {

  @Bean
  @ConditionalOnMissingBean(BuildProperties.class)
  BuildProperties buildProperties() {
    return new BuildProperties(new Properties());
  }
}
