/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.toolkit.persistence.repository;

import java.util.List;

import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.repository.category.TextSearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mpostelnicu
 *
 */
@Transactional
public interface PersonRepository extends TextSearchableRepository<Person, Long> {

    @Query("select p from Person p where p.username = ?1")
    List<Person> findByName(String username);
    
    Person findByEmail(String email);

    Person findBySecret(String secret);
    
    Person findByUsername(String username);
    
    @Override
    @Query("select p from Person p where lower(p.username) like %?1%")
    Page<Person> searchText(String code, Pageable page);
}