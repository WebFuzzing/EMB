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
package org.devgateway.ocds.persistence.repository;

import java.util.List;

import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.devgateway.toolkit.persistence.repository.category.TextSearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author mpostelnicu
 *
 */
@Transactional
@RepositoryRestResource
@PreAuthorize("hasRole('ROLE_PROCURING_ENTITY')")
public interface UserDashboardRepository extends TextSearchableRepository<UserDashboard, Long> {

    @Query("select d from UserDashboard d JOIN d.users p where p.id=:userId")
    Page<UserDashboard> findDashboardsForPersonId(@Param("userId") long  userId, Pageable pageable);
    
    @Query("select d from UserDashboard d JOIN d.users p where p.id=:userId")
    List<UserDashboard> findDashboardsForPersonId(@Param("userId") long  userId);

    @Query("select p.defaultDashboard from Person p where p.id = :userId")
    UserDashboard getDefaultDashboardForPersonId(@Param("userId") long userId);

    @Override
    @Query("select e from  #{#entityName} e where lower(e.name) like %:code%")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Page<UserDashboard> searchText(@Param("code") String code, Pageable page);

    @Override
    @RestResource(exported = false)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<UserDashboard> findAll();

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Page<UserDashboard> findAll(Pageable pageable);

    @Override
    @RestResource(exported = false)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(Long id);

    @Override
    @RestResource(exported = false)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteAll();
    
    @RestResource(exported = true)
    @Override
    UserDashboard getOne(Long id);

}
