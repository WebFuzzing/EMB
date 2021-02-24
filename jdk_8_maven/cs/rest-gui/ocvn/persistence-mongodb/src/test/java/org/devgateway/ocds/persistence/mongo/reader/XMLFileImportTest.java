package org.devgateway.ocds.persistence.mongo.reader;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.toolkit.persistence.mongo.AbstractMongoTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * @author idobre
 * @since 6/27/16
 */
public class XMLFileImportTest extends AbstractMongoTest {
    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    @Qualifier("XMLFileImportDefault")
    private XMLFile xmlFile;

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
    @Ignore
    public void process() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("xml/release.xml").getFile());
        xmlFile.process(file);

        final List<Release> releases = releaseRepository.findAll();
        Assert.assertNotNull(releases);

        final Release release = releaseRepository.findById("xmlimport-123");
        Assert.assertNotNull(release);
        Assert.assertEquals("check field", release.getLanguage(), "en");
    }
}

@Service
@Transactional
class XMLFileImportDefault extends XMLFileImport {
    @Override
    protected Release processRelease(final Release release) {
        return release;
    }

    @Override
    protected AbstractRulesModule getAbstractRulesModule() {
        return new TestRules();
    }

    @Override
    public StringBuffer getMsgBuffer() {
        return null;
    }

    @Override
    public void logMessage(final String message) {

    }

    class TestRules extends AbstractRulesModule {
        @Override
        protected void configure() {
            forPattern("test/release")
                    .createObject().ofType("org.devgateway.ocds.persistence.mongo.Release")
                    .then().setNext("saveRelease");

            forPattern("test/release/id").setBeanProperty().withName("id");

            forPattern("test/release/buyer").createObject().ofType("org.devgateway.ocds.persistence.mongo.Organization")
                    .then().setNext("setBuyer");

            forPattern("test/release/buyer/name").setBeanProperty().withName("name");

            forPattern("test/release/language").setBeanProperty().withName("language");
        }
    }
}
