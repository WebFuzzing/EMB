package org.devgateway.toolkit.web.excelcharts.util;

import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.devgateway.toolkit.web.excelcharts.CustomChartData;

/**
 * @author idobre
 * @since 8/11/16
 *
 *        A factory for different charts data types (like bar chart, area chart)
 */
public interface CustomChartDataFactory {
    /**
     * @return an appropriate CustomChartData instance
     */
    CustomChartData createChartData(ChartType type, String title);
}
