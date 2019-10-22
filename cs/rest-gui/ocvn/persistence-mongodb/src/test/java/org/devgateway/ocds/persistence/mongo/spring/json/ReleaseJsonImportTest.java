package org.devgateway.ocds.persistence.mongo.spring.json;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.toolkit.persistence.mongo.AbstractMongoTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author idobre
 * @since 5/31/16
 */
public class ReleaseJsonImportTest extends AbstractMongoTest {
    @Autowired
    private ReleaseRepository releaseRepository;

    @Before
    public final void setUp() throws Exception {
        // just be sure that the release collection is empty
        releaseRepository.deleteAll();
    }

    @After
    public final void tearDown() {
        // be sure to clean up the release collection
        releaseRepository.deleteAll();
    }

    @Test
    public void importObject() throws Exception {
        final String jsonRelease = "{\n"
                + "    tag: [\"tender\"],\n"
                + "    planning: {\n"
                + "        budget: {\n"
                + "            description: \"budget desc...\",\n"
                + "            amount: {\n"
                + "                amount: 10000.0,\n"
                + "                currency: \"USD\"\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "}";
        final JsonImport releaseJsonImport = new ReleaseJsonImport(releaseRepository, jsonRelease, false);
        final Release release = (Release) releaseJsonImport.importObject();
        final Release releaseById = releaseRepository.findById(release.getId());

        Assert.assertNotNull("Check if we have something in the database", releaseById);
        Assert.assertEquals("Check if the releases are the same", release, releaseById);
    }
}
