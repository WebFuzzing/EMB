/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Genome Nexus.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.genome_nexus;

import org.cbioportal.genome_nexus.web.config.ApiObjectMapper;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Benjamin Gross
 */
@Controller
@SpringBootApplication(scanBasePackages = "org.cbioportal.genome_nexus") // shorthand for @Configuration, @EnableAutoConfiguration, @ComponentScan
@EnableCaching
@EnableSwagger2 // enable swagger2 documentation
public class GenomeNexusAnnotation extends SpringBootServletInitializer implements ErrorController
{
    public static void main(String[] args)
    {
        SpringApplication.run(GenomeNexusAnnotation.class, args);
    }

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public Docket publicApi() {
        // default swagger definition file location: <root>/v2/api-docs?group=variant_annotation
        // default swagger UI location: <root>/swagger-ui.html
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(PublicApi.class))
            .build()
            .useDefaultResponseMessages(false)
            .protocols(new HashSet<>(Arrays.asList("http", "https")))
            .apiInfo(apiInfo());
    }

    @Bean
    public Docket internalApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("internal")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(InternalApi.class))
            .build()
            .useDefaultResponseMessages(false)
            .protocols(new HashSet<>(Arrays.asList("http", "https")))
            .apiInfo(apiInfo());
    }

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        return new MappingJackson2HttpMessageConverter(new ApiObjectMapper());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Genome Nexus API")
            .description("This page shows how to use HTTP requests to " +
                    "access the Genome Nexus API. There are more high level " +
                    "clients available in Python, R, JavaScript, TypeScript " +
                    "and various other languages as well as a command line " +
                    "client to annotate MAF and VCF. See " +
                    "https://docs.genomenexus.org/api.\n\nAside from " +
                    "programmatic clients there are web based tools to annotate " +
                    "variants, see https://docs.genomenexus.org/tools.\n\n " +
                    "We currently only provide long-term support for the " +
                    "'/annotation' endpoint. The other endpoints might " +
                    "change.")
            //.termsOfServiceUrl("http://terms-of-service-url")
            .license("MIT License")
            .licenseUrl("https://github.com/genome-nexus/genome-nexus/blob/master/LICENSE")
            .version("2.0")
            .build();
    }

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    @ResponseStatus(HttpStatus.OK)
    public String error() {
        return "forward:/";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
