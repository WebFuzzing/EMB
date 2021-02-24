package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by daniel on 09-04-2016.
 */

public interface EmployeeDAO extends CrudRepository<Employee,Long> {

    
    public Employee findByUsername(String username);
    
    public List<Employee> findByPrintShop(PrintShop printShop);
}

