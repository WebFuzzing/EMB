/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.toolkit.persistence.mongo.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Run this application only when you need access to Spring Data JPA but without
 * Wicket frontend
 *
 * @author mpostelnicu
 *
 */
@SpringBootApplication
@ComponentScan("org.devgateway")
@PropertySource("classpath:/org/devgateway/toolkit/persistence/mongo/application.properties")
@EnableMongoRepositories(basePackages = "org.devgateway")
@EnableMongoAuditing
@EnableCaching
public class MongoPersistenceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MongoPersistenceApplication.class, args);
    }

    public enum BigDecimalToDoubleConverter implements Converter<BigDecimal, Double> {
        INSTANCE;

        @Override
        public Double convert(final BigDecimal source) {
            return source == null ? null : source.doubleValue();
        }
    }

    public enum DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {
        INSTANCE;

        @Override
        public BigDecimal convert(final Double source) {
            return source != null ? new BigDecimal(source) : null;
        }
    }

    @Bean
    public CustomConversions customConversions() {
        return new CustomConversions(Arrays
                .asList(new Object[] { BigDecimalToDoubleConverter.INSTANCE, DoubleToBigDecimalConverter.INSTANCE }));
    }
}