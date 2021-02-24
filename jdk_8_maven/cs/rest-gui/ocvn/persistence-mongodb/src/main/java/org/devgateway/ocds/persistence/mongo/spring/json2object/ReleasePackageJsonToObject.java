package org.devgateway.ocds.persistence.mongo.spring.json2object;

import org.devgateway.ocds.persistence.mongo.ReleasePackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that transform a JSON object representing a ReleasePackage into a ReleasePackage object
 *
 * @author idobre
 * @since 6/1/16
 */
public class ReleasePackageJsonToObject extends AbstractJsonToObject<ReleasePackage> {
    private ReleasePackage releasePackage;

    public ReleasePackageJsonToObject(final String jsonObject, final Boolean mapDeserializer) {
        super(jsonObject, mapDeserializer);
    }

    public ReleasePackageJsonToObject(final InputStream inputStream, final Boolean mapDeserializer) throws IOException {
        super(inputStream, mapDeserializer);
    }

    public ReleasePackageJsonToObject(final File file, final Boolean mapDeserializer) throws IOException {
        super(file, mapDeserializer);
    }

    @Override
    public ReleasePackage toObject() throws IOException {
        if (releasePackage == null) {
            // Transform JSON String to a Release Object
            releasePackage = this.mapper.readValue(this.jsonObject, ReleasePackage.class);
        }

        return releasePackage;
    }
}
