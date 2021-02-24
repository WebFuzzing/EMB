package org.devgateway.ocds.persistence.mongo.spring.json2object;

import org.devgateway.ocds.persistence.mongo.Release;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that transform a JSON object representing a Release into a Release object
 *
 * @author idobre
 * @since 5/31/16
 */
public class ReleaseJsonToObject extends AbstractJsonToObject<Release> {
    private Release release;

    public ReleaseJsonToObject(final String jsonObject, final Boolean mapDeserializer) {
        super(jsonObject, mapDeserializer);
    }

    public ReleaseJsonToObject(final InputStream inputStream, final Boolean mapDeserializer) throws IOException {
        super(inputStream, mapDeserializer);
    }

    public ReleaseJsonToObject(final File file, final Boolean mapDeserializer) throws IOException {
        super(file, mapDeserializer);
    }

    @Override
    public Release toObject() throws IOException {
        if (release == null) {
            // Transform JSON String to a Release Object
            release = this.mapper.readValue(this.jsonObject, Release.class);
        }

        return release;
    }
}
