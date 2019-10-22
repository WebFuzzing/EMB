package org.devgateway.toolkit.persistence.test;

import org.devgateway.toolkit.persistence.spring.PersistenceApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author mpostelnicu
 *
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
@SpringBootTest(classes = { PersistenceApplication.class })
@TestPropertySource("classpath:test.properties")
public abstract class AbstractPersistenceTest {

}
