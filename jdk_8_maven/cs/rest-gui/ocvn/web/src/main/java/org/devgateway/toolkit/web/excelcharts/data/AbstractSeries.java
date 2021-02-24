package org.devgateway.toolkit.web.excelcharts.data;

import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.xssf.usermodel.charts.AbstractXSSFChartSeries;
import org.devgateway.toolkit.web.excelcharts.CustomChartSeries;

/**
 * @author idobre
 * @since 8/12/16
 */
public abstract class AbstractSeries extends AbstractXSSFChartSeries implements CustomChartSeries {
    protected final int id;

    protected final int order;

    protected final ChartDataSource<?> categories;

    protected final ChartDataSource<? extends Number> values;

    public AbstractSeries(final int id, final int order, final ChartDataSource<?> categories,
            final ChartDataSource<? extends Number> values) {
        this.id = id;
        this.order = order;
        this.categories = categories;
        this.values = values;
    }
}
