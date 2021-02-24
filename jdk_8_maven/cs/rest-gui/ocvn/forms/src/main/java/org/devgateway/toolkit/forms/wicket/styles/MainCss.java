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

import com.google.common.collect.Lists;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

import java.util.List;

/**
 * @author idobre
 * @since 12/2/14
 */

public class MainCss extends CssResourceReference {
    private static final long serialVersionUID = 1L;

    public static final MainCss INSTANCE = new MainCss();

    /**
     * Construct.
     */
    public MainCss() {
        super(MainCss.class, "main.css");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
        return dependencies;
    }
}
