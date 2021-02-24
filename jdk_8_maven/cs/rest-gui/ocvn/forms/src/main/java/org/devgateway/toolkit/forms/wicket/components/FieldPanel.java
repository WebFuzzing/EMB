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
package org.devgateway.toolkit.forms.wicket.components;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * @author mpostelnicu
 *
 */
public class FieldPanel<T> extends GenericPanel<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FieldPanel(final String id) {
        super(id);
    }

    /**
     * @param id
     * @param model
     */
    public FieldPanel(final String id, final IModel<T> model) {
        super(id, model);
    }

}
