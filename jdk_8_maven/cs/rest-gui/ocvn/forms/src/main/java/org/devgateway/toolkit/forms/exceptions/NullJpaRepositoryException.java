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
package org.devgateway.toolkit.forms.exceptions;

/**
 * @author mpostelnicu
 *
 */
public class NullJpaRepositoryException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2779145093766288562L;

    /**
     * 
     */
    public NullJpaRepositoryException() {
        super("jpaRepository is null! Please set the jpaRepository in your constructor");
    }

}
