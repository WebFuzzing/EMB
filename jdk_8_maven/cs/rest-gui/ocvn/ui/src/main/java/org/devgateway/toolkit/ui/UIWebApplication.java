package org.devgateway.toolkit.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author mpostelnicu
 *
 */

@SpringBootApplication(exclude = { EmbeddedMongoAutoConfiguration.class })
@PropertySource("classpath:/org/devgateway/toolkit/ui/application.properties")
public class UIWebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(UIWebApplication.class, args);
    }
}