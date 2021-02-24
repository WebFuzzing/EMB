package org.devgateway.toolkit.web.excelcharts.data;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.xmlbeans.XmlObject;
import org.devgateway.toolkit.web.excelcharts.CustomChartSeries;
import org.devgateway.toolkit.web.excelcharts.util.XSSFChartUtil;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;

/**
 * @author idobre
 * @since 8/12/16
 *
 *        Holds data for a XSSF Bubble Chart.
 */
public class XSSFBubbleChartData extends AbstractXSSFChartData {
    public XSSFBubbleChartData(final String title) {
        super(title);
    }

    @Override
    protected CustomChartSeries createNewSerie(final int id, final int order, final ChartDataSource<?> categories,
            final ChartDataSource<? extends Number> values) {
        return new AbstractSeries(id, order, categories, values) {
            @Override
            public void addToChart(final XmlObject ctChart) {
                final CTBubbleChart ctBubbleChart = (CTBubbleChart) ctChart;
                final CTBubbleSer bubbleSer = ctBubbleChart.addNewSer();

                bubbleSer.addNewIdx().setVal(this.id);
                bubbleSer.addNewOrder().setVal(this.order);

                final CTAxDataSource catDS = bubbleSer.addNewXVal();
                XSSFChartUtil.buildAxDataSource(catDS, this.categories);

                final CTNumDataSource valueDS = bubbleSer.addNewBubbleSize();
                XSSFChartUtil.buildNumDataSource(valueDS, this.values);

                if (isTitleSet()) {
                    bubbleSer.setTx(getCTSerTx());
                }
            }
        };
    }

    @Override
    public void fillChart(final Chart chart, final ChartAxis... axis) {
        if (!(chart instanceof XSSFChart)) {
            throw new IllegalArgumentException("Chart must be instance of XSSFChart");
        }

        final XSSFChart xssfChart = (XSSFChart) chart;
        final CTPlotArea plotArea = xssfChart.getCTChart().getPlotArea();
        final CTBubbleChart bubbleChart = plotArea.addNewBubbleChart();

        for (CustomChartSeries s : series) {
            s.addToChart(bubbleChart);
        }

        for (ChartAxis ax : axis) {
            bubbleChart.addNewAxId().setVal(ax.getId());
        }

        xssfChart.setTitle(this.title);
    }
}
