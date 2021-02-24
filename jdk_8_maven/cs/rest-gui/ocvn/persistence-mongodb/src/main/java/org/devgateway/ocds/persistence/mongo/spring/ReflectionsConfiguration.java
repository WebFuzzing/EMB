/**
 *
 */
package org.devgateway.ocds.persistence.mongo.spring;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mpostelnicu
 *
 */
@Configuration
public class ReflectionsConfiguration {

    protected static final Logger logger = LoggerFactory.getLogger(ReflectionsConfiguration.class);

    @Bean
    public Reflections reflections() {
        logger.debug("Starting reflections scanners...");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("org.devgateway.ocds.persistence.mongo"))
                .setScanners(new SubTypesScanner(), new FieldAnnotationsScanner(), new MethodParameterScanner()));
        logger.debug("Configured reflections bean.");
        return reflections;
    }
}
