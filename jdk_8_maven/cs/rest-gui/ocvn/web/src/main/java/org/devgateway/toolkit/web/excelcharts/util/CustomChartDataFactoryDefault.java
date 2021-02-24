package org.devgateway.toolkit.web.excelcharts.util;

import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.devgateway.toolkit.web.excelcharts.CustomChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFAreaChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFBarChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFBubbleChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFLineChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFPieChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFScatterChartData;
import org.devgateway.toolkit.web.excelcharts.data.XSSFStackedBarChartData;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;

/**
 * @author idobre
 * @since 8/11/16
 */
public class CustomChartDataFactoryDefault implements CustomChartDataFactory {
    /**
     * @return new chart data instance
     */
    @Override
    public CustomChartData createChartData(final ChartType type, final String title) {
        final CustomChartData chartData;
        switch (type) {
        case area:
            chartData = new XSSFAreaChartData(title);
            break;
        case bar:
            XSSFBarChartData barChartData = new XSSFBarChartData(title);
            barChartData.setBarDir(STBarDir.BAR);
            chartData = barChartData;
            break;
        case barcol:
            chartData = new XSSFBarChartData(title);
            break;
        case stackedcol:
            chartData = new XSSFStackedBarChartData(title);
            break;
        case stackedcolpercentage:
            XSSFStackedBarChartData stackedColChartData = new XSSFStackedBarChartData(title);
            stackedColChartData.setBarGrouping(STBarGrouping.PERCENT_STACKED);
            chartData = stackedColChartData;
            break;
        case stackedbar:
            XSSFStackedBarChartData stackedBarChartData = new XSSFStackedBarChartData(title);
            stackedBarChartData.setBarDir(STBarDir.BAR);
            chartData = stackedBarChartData;
            break;
        case stackedbarpercentage:
            XSSFStackedBarChartData stackedBarPercentageChartData = new XSSFStackedBarChartData(title);
            stackedBarPercentageChartData.setBarGrouping(STBarGrouping.PERCENT_STACKED);
            stackedBarPercentageChartData.setBarDir(STBarDir.BAR);
            chartData = stackedBarPercentageChartData;
            break;
        case line:
            chartData = new XSSFLineChartData(title);
            break;
        case pie:
            chartData = new XSSFPieChartData(title);
            break;
        case scatter:
            chartData = new XSSFScatterChartData(title);
            break;
        case bubble:
            chartData = new XSSFBubbleChartData(title);
            break;
        default:
            chartData = null;
        }

        return chartData;
    }
}
