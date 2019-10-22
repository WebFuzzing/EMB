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
package org.devgateway.toolkit.web.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * @author mpostelnicu
 *
 */

@SpringBootApplication(exclude = { EmbeddedMongoAutoConfiguration.class })
@PropertySource("classpath:/org/devgateway/toolkit/web/application.properties")
@ComponentScan("org.devgateway.toolkit")
public class WebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}