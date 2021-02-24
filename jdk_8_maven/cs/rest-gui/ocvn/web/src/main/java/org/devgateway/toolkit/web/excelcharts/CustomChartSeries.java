package org.devgateway.toolkit.web.excelcharts;

import org.apache.poi.ss.usermodel.charts.ChartSeries;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;

/**
 * @author idobre
 * @since 8/12/16
 *
 *        Represents a chart series.
 */
public interface CustomChartSeries extends ChartSeries {
    /**
     * Add a series to the chart.
     *
     * @param ctChart
     *            - chart created from a {@link CTPlotArea}
     */
    void addToChart(XmlObject ctChart);
}
