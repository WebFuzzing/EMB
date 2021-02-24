package org.devgateway.ocds.persistence.mongo.spring.json2object.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.devgateway.ocds.persistence.mongo.Planning;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;

import java.io.IOException;

/**
 * @author idobre
 * @since 9/15/16
 */
public class PlanningDeserializer extends JsonDeserializer<Planning> {
    @Override
    public Planning deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {
        ObjectCodec objectCodec = jsonParser.getCodec();

        return objectCodec.readValue(jsonParser, VNPlanning.class);
    }
}
