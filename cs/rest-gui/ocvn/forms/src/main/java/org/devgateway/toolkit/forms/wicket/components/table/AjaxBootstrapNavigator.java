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

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;

/**
 * @author idobre
 * @since 11/25/14
 */

public class AjaxBootstrapNavigator extends AjaxPagingNavigator {
    protected static Logger logger = Logger.getLogger(AjaxBootstrapNavigator.class);

    private static final long serialVersionUID = -5572869834775798502L;

    private boolean hideFastPagination;

    private Component first;
    private Component next;
    private Component prev;
    private Component last;

    public AjaxBootstrapNavigator(final String id, final IPageable pageable) {
        this(id, pageable, null);
    }

    public AjaxBootstrapNavigator(final String id, final IPageable pageable, final boolean hideFastPagination) {
        this(id, pageable, null, hideFastPagination);
    }

    public AjaxBootstrapNavigator(final String id, final IPageable pageable, final IPagingLabelProvider labelProvider,
            final boolean hideFastPagination) {
        this(id, pageable, labelProvider);

        this.hideFastPagination = hideFastPagination;
    }

    public AjaxBootstrapNavigator(final String id, final IPageable pageable, final IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // hide the first/next/prev/last links
        if (hideFastPagination) {
            first = get("first");
            first.setVisibilityAllowed(false);

            next = get("next");
            next.setVisibilityAllowed(false);

            prev = get("prev");
            prev.setVisibilityAllowed(false);

            last = get("last");
            last.setVisibilityAllowed(false);
        }
    }

    @Override
    protected PagingNavigation newNavigation(final String id, final IPageable pageable,
            final IPagingLabelProvider labelProvider) {
        return new BootstrapPagingNavigation(id, pageable, labelProvider);
    }

    @Override
    protected AbstractLink newPagingNavigationIncrementLink(final String id, final IPageable pageable,
            final int increment) {
        AbstractLink link = super.newPagingNavigationIncrementLink(id, pageable, increment);
        // TODO:disable link
        return link;
    }

    @Override
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, final int pageNumber) {
        AbstractLink link = super.newPagingNavigationLink(id, pageable, pageNumber);
        // TODO:disable link
        return link;
    }
}
