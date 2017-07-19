package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 04-18-2016.
 */

public interface ManagerDAO extends CrudRepository<Manager,Long> {

    
    public Manager findByUsername(String username);
    
    public Manager findByPrintShop(PrintShop printShop);
}
