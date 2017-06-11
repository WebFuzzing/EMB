package org.devgateway.toolkit.web.excelcharts;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author idobre
 * @since 9/8/16
 */
public class ExcelChartSheetDefaultTest {
    private static final List<?> CATEGORIES = Arrays.asList("cat 1", "cat 2", "cat 3", "cat 4", "cat 5");

    private static final List<List<? extends Number>> VALUES =
            Arrays.asList(Arrays.asList(5, 7, 10, 12, 6), Arrays.asList(20, 12, 10, 5, 14));

    private Workbook workbook;

    @Before
    public void setUp() {
        workbook = new XSSFWorkbook();
    }

    @Test
    public void writeCell() throws Exception {
        final ExcelChartSheet excelChartSheet = new ExcelChartSheetDefault(workbook, ChartType.bar.toString());
        final Row row = excelChartSheet.createRow();

        excelChartSheet.writeCell(null, row, 0);
        excelChartSheet.writeCell(Boolean.TRUE, row, 1);
        excelChartSheet.writeCell("text", row, 2);
        excelChartSheet.writeCell(1, row, 3);

        Assert.assertEquals(Cell.CELL_TYPE_BLANK, row.getCell(0).getCellType());
        Assert.assertEquals("Yes", row.getCell(1).getStringCellValue());
        Assert.assertEquals(Cell.CELL_TYPE_STRING, row.getCell(2).getCellType());
        Assert.assertEquals(Cell.CELL_TYPE_NUMERIC, row.getCell(3).getCellType());
    }

    @Test
    public void createRow() throws Exception {
        final ExcelChartSheet excelChartSheet = new ExcelChartSheetDefault(workbook, ChartType.area.toString());

        Row row1 = excelChartSheet.createRow();
        Assert.assertNotNull("check row creation", row1);

        Row row2 = excelChartSheet.createRow(2);
        Assert.assertNotNull("check row creation", row2);
    }

    @Test
    public void createChartAndLegend() throws Exception {
        final ExcelChartSheet excelChartSheet = new ExcelChartSheetDefault(workbook, ChartType.pie.toString());
        Chart chart = excelChartSheet.createChartAndLegend();

        Assert.assertNotNull(chart);
    }

    @Test
    public void getCategoryChartDataSource() throws Exception {
        final ExcelChartSheet excelChartSheet = new ExcelChartSheetDefault(workbook, ChartType.barcol.toString());
        addCategories(excelChartSheet);

        ChartDataSource categoryChartDataSource = excelChartSheet.getCategoryChartDataSource();

        Assert.assertEquals("check count of categories", 5, categoryChartDataSource.getPointCount());
        Assert.assertEquals("check first category", "cat 1", categoryChartDataSource.getPointAt(0));
        Assert.assertEquals("check last category", "cat 5", categoryChartDataSource.getPointAt(4));
    }

    @Test
    public void getValuesChartDataSource() throws Exception {
        final ExcelChartSheet excelChartSheet = new ExcelChartSheetDefault(workbook, ChartType.stackedbar.toString());
        addCategories(excelChartSheet);
        addValues(excelChartSheet);

        List<ChartDataSource<Number>> valuesChartDataSource = excelChartSheet.getValuesChartDataSource();

        Assert.assertEquals("numbers of values data source", 2, valuesChartDataSource.size());
        Assert.assertEquals("check count of values", 5, valuesChartDataSource.get(0).getPointCount());
        Assert.assertEquals("check count of values", 5, valuesChartDataSource.get(1).getPointCount());

        Assert.assertEquals("check first value", 5.0, valuesChartDataSource.get(0).getPointAt(0));
        Assert.assertEquals("check last value", 6.0, valuesChartDataSource.get(0).getPointAt(4));

        Assert.assertEquals("check first value", 20.0, valuesChartDataSource.get(1).getPointAt(0));
        Assert.assertEquals("check last value", 14.0, valuesChartDataSource.get(1).getPointAt(4));
    }

    /**
     * Add a row with the categories.
     */
    private void addCategories(final ExcelChartSheet excelChartSheet) {
        final Row row = excelChartSheet.createRow();
        int coll = 0;
        for (Object category : CATEGORIES) {
            excelChartSheet.writeCell(category, row, coll);
            coll++;
        }
    }

    /**
     * Add one or multiple rows with the values.
     */
    private void addValues(final ExcelChartSheet excelChartSheet) {
        for (List<? extends Number> value : VALUES) {
            final Row row = excelChartSheet.createRow();
            int coll = 0;
            for (Number val : value) {
                excelChartSheet.writeCell(val, row, coll++);
            }
        }
    }
}
