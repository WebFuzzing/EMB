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
package org.devgateway.toolkit.persistence.dao;

/**
 * @author idobre
 * @since 3/10/15
 *
 *        An entity that has a {@link String} designation property
 */
public interface Searchable {
    String getDesignation();

    void setDesignation(String designation);
}
