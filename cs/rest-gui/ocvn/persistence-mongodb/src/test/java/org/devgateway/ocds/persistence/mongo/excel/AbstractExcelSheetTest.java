package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author idobre
 * @since 6/8/16
 */
public class AbstractExcelSheetTest {
    private class MockExcelSheet extends AbstractExcelSheet {
        MockExcelSheet(final Workbook workbook) {
            super(workbook);
        }

        @Override
        public void writeRow(final Object object, final Row row) {
        }

        @Override
        public void writeSheet(final List<Object> objects) {
        }

        @Override
        public void emptySheet() {
        }
    }

    @Test
    public void writeCell() throws Exception {
        final Workbook workbook = new XSSFWorkbook();
        final ExcelSheet excelSheet = new MockExcelSheet(workbook);
        final Sheet sheet = workbook.createSheet("sheet");
        final Row row = sheet.createRow(0);

        excelSheet.writeCell(null, row, 0);
        excelSheet.writeCell(Boolean.TRUE, row, 1);
        excelSheet.writeCell("text", row, 2);
        excelSheet.writeCell(1, row, 3);

        Assert.assertEquals(Cell.CELL_TYPE_BLANK, row.getCell(0).getCellType());
        Assert.assertEquals("Yes", row.getCell(1).getStringCellValue());
        Assert.assertEquals(Cell.CELL_TYPE_STRING, row.getCell(2).getCellType());
        Assert.assertEquals(Cell.CELL_TYPE_NUMERIC, row.getCell(3).getCellType());
    }
}
