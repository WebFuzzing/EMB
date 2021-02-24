package org.devgateway.toolkit.web.spring;

import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * We only allow to expose repositories that are annotated
 * 
 * @author mpostelnicu
 * http://docs.spring.io/spring-data/rest/docs/current/reference/html/#_which_repositories_get_exposed_by_defaults
 */
@Configuration
public class CustomRestMvcConfiguration {

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {

        return new RepositoryRestConfigurerAdapter() {

            @Override
            public void configureRepositoryRestConfiguration(final RepositoryRestConfiguration config) {
                config.setRepositoryDetectionStrategy(RepositoryDetectionStrategies.ANNOTATED);
                config.exposeIdsFor(UserDashboard.class);
            }
        };
    }
}