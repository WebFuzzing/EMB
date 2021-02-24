/**
 *
 */
package org.devgateway.toolkit.persistence.test;

import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mpostelnicu
 *
 */
public class PersonRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testFindByname() {
        personRepository.count();
    }

}
