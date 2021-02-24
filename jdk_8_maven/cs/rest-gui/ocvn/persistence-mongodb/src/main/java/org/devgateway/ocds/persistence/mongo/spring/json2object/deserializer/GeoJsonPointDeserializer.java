package org.devgateway.ocds.persistence.mongo.spring.json2object.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

/**
 * @author idobre
 * @since 9/15/16
 */
public class GeoJsonPointDeserializer extends JsonDeserializer<GeoJsonPoint> {
    @Override
    public GeoJsonPoint deserialize(final JsonParser jsonParser, final DeserializationContext ctxt)
            throws IOException {

        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);
        ArrayNode coordinate = (ArrayNode) jsonNode.get("coordinates");

        return new GeoJsonPoint(coordinate.get(0).asDouble(), coordinate.get(1).asDouble());
    }
}
