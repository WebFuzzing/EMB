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
package org.devgateway.toolkit.forms.wicket.styles;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author idobre
 * @since 1/13/15
 *
 *        Load an empty CSS file - this instance is used to have more control
 *        when we load bootstrap.css file
 */
public class EmptyCss extends CssResourceReference {
    private static final long serialVersionUID = 1L;

    public static final EmptyCss INSTANCE = new EmptyCss();

    /**
     * Construct.
     */
    public EmptyCss() {
        super(EmptyCss.class, "empty.css");
    }
}
