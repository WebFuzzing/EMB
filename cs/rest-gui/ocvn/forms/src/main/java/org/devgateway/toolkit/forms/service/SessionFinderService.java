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
package org.devgateway.toolkit.forms.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import nl.dries.wicket.hibernate.dozer.DozerModel;
import nl.dries.wicket.hibernate.dozer.SessionFinder;

/**
 * Spring Service allowing access to hibernate session. This is needed by
 * {@link DozerModel}
 * 
 * @author mpostelnicu
 * @see DozerModel
 */
@Component
public class SessionFinderService implements SessionFinder {

    @PersistenceContext
    private EntityManager em;

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.dries.wicket.hibernate.dozer.SessionFinder#getHibernateSession(java
     * .lang.Class)
     */
    @Override
    public Session getHibernateSession(final Class<?> clazz) {
        return em.unwrap(Session.class);
    }

}