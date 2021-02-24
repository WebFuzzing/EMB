package org.devgateway.toolkit.web.excelcharts.data;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.xmlbeans.XmlObject;
import org.devgateway.toolkit.web.excelcharts.CustomChartSeries;
import org.devgateway.toolkit.web.excelcharts.util.XSSFChartUtil;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;

/**
 * @author idobre
 * @since 8/12/16
 *
 *        Holds data for a XSSF Line Chart.
 */
public class XSSFLineChartData extends AbstractXSSFChartData {
    public XSSFLineChartData(final String title) {
        super(title);
    }

    @Override
    protected CustomChartSeries createNewSerie(final int id, final int order, final ChartDataSource<?> categories,
            final ChartDataSource<? extends Number> values) {
        return new AbstractSeries(id, order, categories, values) {
            @Override
            public void addToChart(final XmlObject ctChart) {
                final CTLineChart ctLineChart = (CTLineChart) ctChart;
                final CTLineSer ctLineSer = ctLineChart.addNewSer();

                ctLineSer.addNewIdx().setVal(this.id);
                ctLineSer.addNewOrder().setVal(this.order);

                // No marker symbol on the chart line.
                ctLineSer.addNewMarker().addNewSymbol().setVal(STMarkerStyle.CIRCLE);

                final CTAxDataSource catDS = ctLineSer.addNewCat();
                XSSFChartUtil.buildAxDataSource(catDS, this.categories);

                final CTNumDataSource valueDS = ctLineSer.addNewVal();
                XSSFChartUtil.buildNumDataSource(valueDS, this.values);

                if (isTitleSet()) {
                    ctLineSer.setTx(getCTSerTx());
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
        final CTLineChart lineChart = plotArea.addNewLineChart();
        lineChart.addNewVaryColors().setVal(false);

        for (CustomChartSeries s : series) {
            s.addToChart(lineChart);
        }

        for (ChartAxis ax : axis) {
            lineChart.addNewAxId().setVal(ax.getId());
        }

        xssfChart.setTitle(this.title);

        // add grid lines
        CTCatAx[] ctCatAx = plotArea.getCatAxArray();
        if (ctCatAx.length != 0) {
            ctCatAx[0].addNewMajorGridlines().addNewSpPr().addNewSolidFill();
        }

        CTValAx[] ctValAx = plotArea.getValAxArray();
        if (ctValAx.length != 0) {
            ctValAx[0].addNewMajorGridlines().addNewSpPr().addNewSolidFill();
        }
    }
}
