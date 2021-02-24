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
package org.devgateway.ocvn.persistence.repository;

import org.devgateway.ocvn.persistence.dao.VietnamImportSourceFiles;
import org.devgateway.toolkit.persistence.repository.category.TextSearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mpostelnicu
 *
 */
@Transactional
public interface VietnamImportSourceFilesRepository extends TextSearchableRepository<VietnamImportSourceFiles, Long> {

    @Override
    @Query("select a from  #{#entityName} a where lower(a.name) like %:code%")
    Page<VietnamImportSourceFiles> searchText(@Param("code") String code, Pageable page);
}
