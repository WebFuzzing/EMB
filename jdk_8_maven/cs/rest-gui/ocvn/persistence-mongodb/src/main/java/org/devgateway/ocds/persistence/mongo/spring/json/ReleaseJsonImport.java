package org.devgateway.ocds.persistence.mongo.spring.json;

import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.json2object.JsonToObject;
import org.devgateway.ocds.persistence.mongo.spring.json2object.ReleaseJsonToObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

/**
 * Class that imports a JSON object (from a string/file) into the database
 *
 * @author idobre
 * @since 5/31/16
 */
@Transactional
public class ReleaseJsonImport implements JsonImport<Release> {
    private static Logger logger = Logger.getLogger(ReleaseJsonImport.class);

    private final ReleaseRepository releaseRepository;

    private final JsonToObject releaseJsonToObject;

    public ReleaseJsonImport(final ReleaseRepository releaseRepository,
                             final String releaseJsonToObject,
                             final Boolean mapDeserializer) {
        this.releaseRepository = releaseRepository;
        this.releaseJsonToObject = new ReleaseJsonToObject(releaseJsonToObject, mapDeserializer);
    }

    public ReleaseJsonImport(final ReleaseRepository releaseRepository,
                             final File file,
                             final Boolean mapDeserializer) throws IOException {
        this.releaseRepository = releaseRepository;
        this.releaseJsonToObject = new ReleaseJsonToObject(file, mapDeserializer);
    }

    @Override
    public Release importObject() throws IOException {
        Release release = (Release) releaseJsonToObject.toObject();
        release = releaseRepository.save(release);

        return release;
    }

    @Override
    public void logMessage(final String message) {

    }
}
