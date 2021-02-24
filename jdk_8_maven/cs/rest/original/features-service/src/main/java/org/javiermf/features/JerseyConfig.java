package org.javiermf.features;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.javiermf.features.services.rest.ProductsResource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(ProductsResource.class);
        configureSwagger();
    }

    private void configureSwagger() {
        register(ApiListingResource.class);

        register(SwaggerSerializers.class);
//        register(JacksonFeature.class);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage("org.javiermf.features.services.rest");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
    }
}

