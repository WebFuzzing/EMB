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

import com.mongodb.DBObject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Run this application only when you need access to Spring Data JPA but without
 * Wicket frontend
 *
 * @author mpostelnicu
 */
@SpringBootApplication(exclude = { EmbeddedMongoAutoConfiguration.class })
@ComponentScan("org.devgateway")
@PropertySource("classpath:/org/devgateway/toolkit/persistence/mongo/application.properties")
@EnableCaching
public class MongoPersistenceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MongoPersistenceApplication.class, args);
    }

    @Bean
    public CustomConversions customConversions() {
        return new CustomConversions(Arrays
                .asList(new Object[]{BigDecimalToDoubleConverter.INSTANCE, DoubleToBigDecimalConverter.INSTANCE,
                        DbObjectToGeoJsonPointConverter.INSTANCE}));
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

    public enum DbObjectToGeoJsonPointConverter implements Converter<DBObject, GeoJsonPoint> {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Override
        @SuppressWarnings("unchecked")
        public GeoJsonPoint convert(DBObject source) {

            if (source == null) {
                return null;
            }

            if (source.get("type") == null) {
                return null;
            }

            Assert.isTrue(ObjectUtils.nullSafeEquals(source.get("type"), "Point"),
                    String.format("Cannot convert type '%s' to Point.", source.get("type")));

            List<Number> dbl = (List<Number>) source.get("coordinates");
            return new GeoJsonPoint(dbl.get(0).doubleValue(), dbl.get(1).doubleValue());
        }
    }
}