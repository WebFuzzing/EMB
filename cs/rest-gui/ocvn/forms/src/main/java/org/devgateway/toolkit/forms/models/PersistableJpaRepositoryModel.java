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
package org.devgateway.toolkit.forms.models;

import org.apache.wicket.model.LoadableDetachableModel;
import org.devgateway.toolkit.forms.wicket.providers.SortableJpaRepositoryDataProvider;
import org.devgateway.toolkit.persistence.dao.GenericPersistable;
import org.springframework.data.jpa.repository.JpaRepository;

import nl.dries.wicket.hibernate.dozer.DozerModel;

/**
 * USE THIS ONLY FOR {@link SortableJpaRepositoryDataProvider}S Use
 * {@link DozerModel} for editing complex forms
 * 
 * @author mpostelnicu
 *
 * @param <T>
 *            the type of the entity to be accessed
 */
public class PersistableJpaRepositoryModel<T extends GenericPersistable> extends LoadableDetachableModel<T> {
    private static final long serialVersionUID = -3668189792112474025L;
    private Long id;
    private JpaRepository<T, Long> jpaRepository;

    public PersistableJpaRepositoryModel(final Long id, final JpaRepository<T, Long> jpaRepository) {
        super();
        this.id = id;
        this.jpaRepository = jpaRepository;
    }

    public PersistableJpaRepositoryModel(final T t, final JpaRepository<T, Long> jpaRepository) {
        super(t);
        this.id = t.getId();
        this.jpaRepository = jpaRepository;
    }

    @Override
    protected T load() {
        return jpaRepository.findOne(id);

    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof PersistableJpaRepositoryModel) {
            PersistableJpaRepositoryModel<T> other = (PersistableJpaRepositoryModel<T>) obj;
            return other.id == id;
        }
        return false;
    }
}