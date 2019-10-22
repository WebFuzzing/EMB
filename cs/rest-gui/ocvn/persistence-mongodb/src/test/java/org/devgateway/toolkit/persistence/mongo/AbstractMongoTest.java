package org.devgateway.toolkit.persistence.mongo;

import org.devgateway.toolkit.persistence.mongo.spring.MongoPersistenceApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
@SpringBootTest(classes = { MongoPersistenceApplication.class })
@TestPropertySource("classpath:test.properties")
@AutoConfigureCache
/**
 * Superclass for all integration tests that use MongoDB. Adding this as a
 * superclass will ensure the tests are run with the "integration" spring
 * profile. This will enable a special embedded mongodb server that can be used
 * just during the test.
 *
 * @author mpostelnicu
 *
 */
public abstract class AbstractMongoTest {

}
