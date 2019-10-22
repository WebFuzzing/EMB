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
package org.devgateway.toolkit.forms.wicket.page;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author idobre
 * @since 12/1/14
 */

public class Header extends Panel {
    private static final long serialVersionUID = 1L;

    protected static Logger logger = Logger.getLogger(Header.class);

    public Header(final String markupId) {
        this(markupId, null);
    }

    public Header(final String markupId, final PageParameters parameters) {
        super(markupId);
    }
}
