package org.devgateway.toolkit.web.excelcharts.data;

import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.devgateway.toolkit.web.excelcharts.CustomChartData;
import org.devgateway.toolkit.web.excelcharts.CustomChartSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * @author idobre
 * @since 8/12/16
 *
 *        General class for creating a CustomChartData object.
 */
public abstract class AbstractXSSFChartData implements CustomChartData {
    /**
     * List of all data series.
     */
    protected final List<CustomChartSeries> series;

    /**
     * Chart title.
     */
    protected final String title;

    public AbstractXSSFChartData(final String title) {
        this.title = title;
        series = new ArrayList<>();
    }

    public AbstractXSSFChartData() {
        this(null);
    }

    @Override
    public CustomChartSeries addSeries(final ChartDataSource<?> categoryAxisData,
            final ChartDataSource<? extends Number> values) {
        return this.addSeries(null, categoryAxisData, values);
    }

    @Override
    public CustomChartSeries addSeries(final String title, final ChartDataSource<?> categoryAxisData,
            final ChartDataSource<? extends Number> values) {
        if (!values.isNumeric()) {
            throw new IllegalArgumentException("Value data source must be numeric.");
        }

        int numOfSeries = series.size();
        final CustomChartSeries newSeries = createNewSerie(numOfSeries, numOfSeries, categoryAxisData, values);
        if (title != null) {
            newSeries.setTitle(title);
        }
        series.add(newSeries);

        return newSeries;
    }

    /**
     * Add a new Serie specific to each AbstractXSSFChartData implementation.
     */
    protected abstract CustomChartSeries createNewSerie(int id, int order,
            ChartDataSource<?> categories, ChartDataSource<? extends Number> values);
}
