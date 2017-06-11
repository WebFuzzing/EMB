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
package org.devgateway.toolkit.persistence.repository.category;

import java.io.Serializable;

import org.devgateway.toolkit.persistence.repository.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author idobre
 * @since 11/20/14
 */

@NoRepositoryBean
@Transactional
public interface TextSearchableRepository<T, ID extends Serializable> extends BaseJpaRepository<T, ID> {

    @Override
    Page<T> findAll(Pageable pageable);

    Page<T> searchText(String code, Pageable page);
}
