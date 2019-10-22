package org.devgateway.toolkit.web.excelcharts;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.charts.XSSFCategoryAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFChartAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFValueAxis;
import org.junit.Assert;
import org.junit.Test;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;

import java.util.Arrays;
import java.util.List;

/**
 * @author idobre
 * @since 9/8/16
 */
public class ExcelChartDefaultTest {
    private static final List<?> CATEGORIES = Arrays.asList("cat 1", "cat 2", "cat 3", "cat 4", "cat 5");

    private static final List<List<? extends Number>> VALUES =
            Arrays.asList(Arrays.asList(5, 7, 10, 12, 6), Arrays.asList(20, 12, 10, 5, 14));

    @Test
    public void createWorkbook() throws Exception {
        final ExcelChart excelChart = new ExcelChartDefault("line chart", ChartType.line, CATEGORIES, VALUES);
        excelChart.configureSeriesTitle(Arrays.asList("foo", "bar"));
        final Workbook workbook = excelChart.createWorkbook();
        Assert.assertNotNull(workbook);

        final Sheet sheet = workbook.getSheet(ChartType.line.toString());
        Assert.assertNotNull(sheet);

        final XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
        final List<XSSFChart> charts = drawing.getCharts();
        Assert.assertEquals("number of charts", 1, charts.size());

        final XSSFChart chart = charts.get(0);
        Assert.assertEquals("chart title", "line chart", chart.getTitle().getString());

        final CTChart ctChart = chart.getCTChart();
        Assert.assertEquals("We should not have any area chart", 0, ctChart.getPlotArea().getAreaChartArray().length);
        Assert.assertEquals("Check if we have 1 line chart", 1, ctChart.getPlotArea().getLineChartArray().length);
        Assert.assertEquals("Check that we have a legend and that it's position is bottom", STLegendPos.B,
                ctChart.getLegend().getLegendPos().getVal());

        // check the actual chart data
        final CTLineChart ctLineChart = ctChart.getPlotArea().getLineChartArray()[0];
        final CTLineSer[] ctLineSer = ctLineChart.getSerArray();
        Assert.assertEquals("Check number of CTLineSer", 2, ctLineSer.length);
        Assert.assertEquals("check first series title", "foo", ctLineSer[0].getTx().getV());
        Assert.assertEquals("check second series title", "bar", ctLineSer[1].getTx().getV());

        final CTAxDataSource cat1 = ctLineSer[0].getCat();
        Assert.assertEquals("check first category", "cat 1", cat1.getStrRef().getStrCache().getPtArray()[0].getV());
        Assert.assertEquals("check last category", "cat 5", cat1.getStrRef().getStrCache().getPtArray()[4].getV());
        final CTAxDataSource cat2 = ctLineSer[1].getCat();
        Assert.assertEquals("check first category", "cat 1", cat2.getStrRef().getStrCache().getPtArray()[0].getV());
        Assert.assertEquals("check last category", "cat 5", cat2.getStrRef().getStrCache().getPtArray()[4].getV());

        final CTNumDataSource val1 = ctLineSer[0].getVal();
        Assert.assertEquals("check first value", "5.0", val1.getNumRef().getNumCache().getPtArray()[0].getV());
        Assert.assertEquals("check last value", "6.0", val1.getNumRef().getNumCache().getPtArray()[4].getV());
        final CTNumDataSource val2 = ctLineSer[1].getVal();
        Assert.assertEquals("check first value", "20.0", val2.getNumRef().getNumCache().getPtArray()[0].getV());
        Assert.assertEquals("check last value", "14.0", val2.getNumRef().getNumCache().getPtArray()[4].getV());

        final List<? extends XSSFChartAxis> axis = chart.getAxis();
        Assert.assertEquals("number of axis", 2, axis.size());
        Assert.assertTrue("category axis", axis.get(0) instanceof XSSFCategoryAxis);
        Assert.assertTrue("value axis", axis.get(1) instanceof XSSFValueAxis);
    }
}
