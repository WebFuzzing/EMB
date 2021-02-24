package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 04-18-2016.
 */

public interface PrintShopDAO extends CrudRepository<PrintShop,Long> {

    
    public PrintShop findByName(String name);
}
