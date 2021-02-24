package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 04-04-2016.
 */

public interface ConsumerDAO extends CrudRepository<Consumer,Long> {

    
    public Consumer findByUsername(String username);

    
    public Consumer findByEmail(String email);
}
