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
/**
 * 
 */
package org.devgateway.toolkit.persistence.mongo.dao;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * @author mpostelnicu
 *
 */
public class GenericMongoPersistable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

}
