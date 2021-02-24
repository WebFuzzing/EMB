package org.devgateway.toolkit.forms.wicket.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;

/**
 * Filter form that resets current page to 0 after filtering is changed or
 * applied.
 *
 * Created by octavian on 15.04.2016.
 */
public class ResettingFilterForm<T> extends FilterForm<T> {

    private static final long serialVersionUID = 7877429240496220944L;
    private DataTable<?, ?> dataTable;

    public ResettingFilterForm(final String id, final IFilterStateLocator<T> locator, final DataTable<?, ?> dataTable) {
        super(id, locator);
        this.dataTable = dataTable;
    }

    @Override
    protected void onModelChanged() {
        dataTable.setCurrentPage(0);
    }

    @Override
    protected void onSubmit() {
        dataTable.setCurrentPage(0);
    }
}
