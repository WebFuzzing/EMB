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
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 * @author idobre
 * @since 11/25/14
 */

public class AjaxBootstrapNavigationToolbar extends AbstractToolbar {
    private static final long serialVersionUID = 230663553625059960L;

    public AjaxBootstrapNavigationToolbar(final DataTable<?, ?> table) {
        super(table);

        WebMarkupContainer span = new WebMarkupContainer("span");
        this.add(span);
        span.add(AttributeModifier.replace("colspan", new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return String.valueOf(table.getColumns().size());
            }
        }));

        span.add(new Component[] { this.newPagingNavigator("navigator", table) });
    }

    protected PagingNavigator newPagingNavigator(final String navigatorId, final DataTable<?, ?> table) {
        return new AjaxBootstrapNavigator(navigatorId, table) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onAjaxEvent(final AjaxRequestTarget target) {
                target.add(table);
            }
        };
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.setVisible(this.getTable().getPageCount() > 1L);
    }
}
