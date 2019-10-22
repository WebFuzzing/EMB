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

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author idobre
 * @since 11/25/14
 *
 *        Table that uses Ajax-enhanced navigator with Twitter bootstrap styles
 */

public class AjaxFallbackBootstrapDataTable<T, S> extends DataTable<T, S> {
    private static final long serialVersionUID = -4423767033850245605L;

    public AjaxFallbackBootstrapDataTable(final String id, final List<? extends IColumn<T, S>> columns,
            final ISortableDataProvider<T, S> dataProvider, final int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
        this.setOutputMarkupId(true);
        this.setVersioned(false);
        this.addTopToolbar(new AjaxFallbackHeadersToolbar<S>(this, dataProvider));
        this.addBottomToolbar(new AjaxBootstrapNavigationToolbar(this));
        this.addBottomToolbar(new NoRecordsToolbar(this));
    }

    @Override
    protected Item<T> newRowItem(final String id, final int index, final IModel<T> model) {
        return new OddEvenItem<T>(id, index, model);
    }
}
