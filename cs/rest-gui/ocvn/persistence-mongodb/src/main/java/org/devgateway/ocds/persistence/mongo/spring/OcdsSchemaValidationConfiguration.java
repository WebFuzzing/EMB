package org.devgateway.ocds.persistence.mongo.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcdsSchemaValidationConfiguration {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Bean("ocdsSchemaValidator")
    public OcdsSchemaValidatorService ocdsSchemaValidatorService() {
        OcdsSchemaValidatorService jsonSchemaValidation = new OcdsSchemaValidatorService(jacksonObjectMapper);
        jsonSchemaValidation
                .withJsonPatches(OcdsSchemaValidatorService.OCDS_LOCATION_PATCH_LOCATION,
                        OcdsSchemaValidatorService.OCDS_BID_EXTENSION)
                .init();
        return jsonSchemaValidation;
    }

    @Bean("ocdsSchemaAllRequiredValidator")
    public OcdsSchemaValidatorService ocdsSchemaAllRequiredValidatorService() {
        OcdsSchemaValidatorService jsonSchemaValidation = new OcdsSchemaValidatorService(jacksonObjectMapper,
                OcdsSchemaValidatorService.OCDS_SCHEMA_ALL_REQUIRED);
        jsonSchemaValidation
                .withJsonPatches(OcdsSchemaValidatorService.OCDS_LOCATION_PATCH_LOCATION,
                        OcdsSchemaValidatorService.OCDS_BID_EXTENSION)
                .init();
        return jsonSchemaValidation;
    }

}
