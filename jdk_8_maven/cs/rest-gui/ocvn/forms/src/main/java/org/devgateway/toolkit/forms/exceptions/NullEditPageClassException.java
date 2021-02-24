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
public class NullEditPageClassException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 8455626306350234992L;

    /**
     * 
     */
    public NullEditPageClassException() {
        super("editPageClass is null! Please set the editPageClass in your constructor");
    }

}
