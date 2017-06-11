package org.devgateway.ocds.persistence.mongo.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcdsSchemaValidationConfiguration {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Bean
    public OcdsSchemaValidatorService ocdsSchemaValidatorService() {
        OcdsSchemaValidatorService jsonSchemaValidation = new OcdsSchemaValidatorService(jacksonObjectMapper);
        jsonSchemaValidation
                .withJsonPatches(OcdsSchemaValidatorService.OCDS_LOCATION_PATCH_LOCATION)
                .init();
        return jsonSchemaValidation;
    }

}
