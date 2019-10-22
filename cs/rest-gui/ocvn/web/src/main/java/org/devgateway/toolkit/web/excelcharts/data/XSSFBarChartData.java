package org.devgateway.toolkit.web.excelcharts.data;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.xmlbeans.XmlObject;
import org.devgateway.toolkit.web.excelcharts.CustomChartSeries;
import org.devgateway.toolkit.web.excelcharts.util.XSSFChartUtil;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrossBetween;

/**
 * @author idobre
 * @since 8/8/16
 *
 *        Holds data for a XSSF Bar Chart.
 */
public class XSSFBarChartData extends AbstractXSSFChartData {
    protected STBarDir.Enum barDir = STBarDir.COL;

    public XSSFBarChartData(final String title) {
        super(title);
    }

    @Override
    protected CustomChartSeries createNewSerie(final int id, final int order, final ChartDataSource<?> categories,
            final ChartDataSource<? extends Number> values) {
        return new AbstractSeries(id, order, categories, values) {
            @Override
            public void addToChart(final XmlObject ctChart) {
                final CTBarChart ctBarChart = (CTBarChart) ctChart;
                final CTBarSer ctBarSer = ctBarChart.addNewSer();

                ctBarSer.addNewIdx().setVal(this.id);
                ctBarSer.addNewOrder().setVal(this.order);

                final CTAxDataSource catDS = ctBarSer.addNewCat();
                XSSFChartUtil.buildAxDataSource(catDS, this.categories);

                final CTNumDataSource valueDS = ctBarSer.addNewVal();
                XSSFChartUtil.buildNumDataSource(valueDS, this.values);

                if (isTitleSet()) {
                    ctBarSer.setTx(getCTSerTx());
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
        final CTBarChart barChart = plotArea.addNewBarChart();

        barChart.addNewVaryColors().setVal(false);

        // set bars orientation
        barChart.addNewBarDir().setVal(barDir);

        xssfChart.setTitle(this.title);

        CTValAx[] ctValAx = plotArea.getValAxArray();
        if (ctValAx.length != 0) {
            ctValAx[0].addNewMajorGridlines().addNewSpPr().addNewSolidFill();
            ctValAx[0].getCrossBetween().setVal(STCrossBetween.BETWEEN);
        }

        for (CustomChartSeries s : series) {
            s.addToChart(barChart);
        }

        for (ChartAxis ax : axis) {
            barChart.addNewAxId().setVal(ax.getId());
        }
    }

    public void setBarDir(final STBarDir.Enum barDir) {
        this.barDir = barDir;
    }
}
