package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.Admin;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 14-04-2016.
 */

public interface AdminDAO extends CrudRepository<Admin,Long> {

    
    public Admin findByUsername(String username);
}
