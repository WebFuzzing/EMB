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
package org.devgateway.toolkit.forms.wicket.components.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 * @author idobre
 * @since 11/25/14
 */

public class BootstrapPagingNavigation extends AjaxPagingNavigation {
    private static final long serialVersionUID = 5153575341725990502L;

    public BootstrapPagingNavigation(final String id, final IPageable pageable) {
        super(id, pageable);
    }

    public BootstrapPagingNavigation(final String id, final IPageable pageable,
            final IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }

    @Override
    protected void populateItem(final LoopItem loopItem) {
        super.populateItem(loopItem);

        final long pageIndex = getStartIndex() + loopItem.getIndex();

        // Add disabled class to enclosing list item.
        loopItem.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                if (pageable.getCurrentPage() == pageIndex) {
                    return "active";
                } else {
                    return "";
                }
            }
        }));

        // Do not mask disabled link by em tag.
        // ((AbstractLink)
        // loopItem.get("pageLink")).setBeforeDisabledLink("").setAfterDisabledLink("");
    }
}
