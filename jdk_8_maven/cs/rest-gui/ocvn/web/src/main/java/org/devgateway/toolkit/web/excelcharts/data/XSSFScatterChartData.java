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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STScatterStyle;

/**
 * @author idobre
 * @since 8/12/16
 *
 *        Holds data for a XSSF Scatter Chart.
 */
public class XSSFScatterChartData extends AbstractXSSFChartData {
    public XSSFScatterChartData(final String title) {
        super(title);
    }

    @Override
    protected CustomChartSeries createNewSerie(final int id, final int order, final ChartDataSource<?> categories,
            final ChartDataSource<? extends Number> values) {
        return new AbstractSeries(id, order, categories, values) {
            @Override
            public void addToChart(final XmlObject ctChart) {
                final CTScatterChart ctScatterChart = (CTScatterChart) ctChart;
                final CTScatterSer scatterSer = ctScatterChart.addNewSer();

                scatterSer.addNewIdx().setVal(this.id);
                scatterSer.addNewOrder().setVal(this.order);

                final CTAxDataSource catDS = scatterSer.addNewXVal();
                XSSFChartUtil.buildAxDataSource(catDS, this.categories);

                final CTNumDataSource valueDS = scatterSer.addNewYVal();
                XSSFChartUtil.buildNumDataSource(valueDS, this.values);

                if (isTitleSet()) {
                    scatterSer.setTx(getCTSerTx());
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
        final CTScatterChart scatterChart = plotArea.addNewScatterChart();
        addStyle(scatterChart);

        for (CustomChartSeries s : series) {
            s.addToChart(scatterChart);
        }

        for (ChartAxis ax : axis) {
            scatterChart.addNewAxId().setVal(ax.getId());
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

    private static void addStyle(final CTScatterChart ctScatterChart) {
        final CTScatterStyle scatterStyle = ctScatterChart.addNewScatterStyle();
        scatterStyle.setVal(STScatterStyle.LINE_MARKER);
    }
}
