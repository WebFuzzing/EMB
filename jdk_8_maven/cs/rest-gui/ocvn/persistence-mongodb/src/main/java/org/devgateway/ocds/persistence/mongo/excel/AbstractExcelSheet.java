package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Class that prepares the default Styles and Fonts for Excel cells.
 *
 * @author idobre
 * @since 6/7/16
 */
public abstract class AbstractExcelSheet implements ExcelSheet {
    protected final Workbook workbook;

    private Font dataFont;

    private Font headerFont;

    private Font linkFont;

    private final CellStyle dataStyleCell;

    private final CellStyle headerStyleCell;

    private final CellStyle linkStyleCell;

    private final CreationHelper createHelper;

    // declare only one cell object reference
    private Cell cell = null;

    public AbstractExcelSheet(final Workbook workbook) {
        this.workbook = workbook;

        // get the styles from workbook without creating them again (by default the workbook has already 1 style)
        if (workbook.getNumCellStyles() > 1) {
            this.dataStyleCell = workbook.getCellStyleAt((short) 1);

            this.headerStyleCell = workbook.getCellStyleAt((short) 2);

            this.linkStyleCell = workbook.getCellStyleAt((short) 3);
        } else {
            // init the fonts and styles
            this.dataFont = this.workbook.createFont();
            this.dataFont.setFontHeightInPoints((short) 12);
            this.dataFont.setFontName("Times New Roman");
            this.dataFont.setColor(HSSFColor.BLACK.index);

            this.headerFont = this.workbook.createFont();
            this.headerFont.setFontHeightInPoints((short) 14);
            this.headerFont.setFontName("Times New Roman");
            this.headerFont.setColor(HSSFColor.BLACK.index);
            this.headerFont.setBold(true);

            this.linkFont = this.workbook.createFont();
            this.linkFont.setFontHeightInPoints((short) 12);
            this.linkFont.setFontName("Times New Roman");
            // by default hyperlinks are blue and underlined
            this.linkFont.setColor(HSSFColor.BLUE.index);
            this.linkFont.setUnderline(Font.U_SINGLE);

            this.dataStyleCell = this.workbook.createCellStyle();
            this.dataStyleCell.setAlignment(CellStyle.ALIGN_LEFT);
            this.dataStyleCell.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            this.dataStyleCell.setWrapText(true);
            this.dataStyleCell.setFont(this.dataFont);

            this.headerStyleCell = this.workbook.createCellStyle();
            this.headerStyleCell.setAlignment(CellStyle.ALIGN_CENTER);
            this.headerStyleCell.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            this.headerStyleCell.setWrapText(true);
            this.headerStyleCell.setFont(this.headerFont);

            this.linkStyleCell = this.workbook.createCellStyle();
            this.linkStyleCell.setAlignment(CellStyle.ALIGN_LEFT);
            this.linkStyleCell.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            this.linkStyleCell.setWrapText(true);
            this.linkStyleCell.setFont(this.linkFont);
        }

        this.createHelper = workbook.getCreationHelper();
    }

    /**
     * Creates a cell and tries to determine it's type based on the value type
     *
     * there is only one Cell object otherwise the Heap Space will fill really quickly
     *
     * @param value
     * @param row
     * @param column
     */
    @Override
    public void writeCell(final Object value, final Row row, final int column) {
        // try to determine the cell type based on the object value
        // if nothing matches then use 'CELL_TYPE_STRING' as type and call the object toString() function.
        //      * don't create any cell if the value is null (Cell.CELL_TYPE_BLANK)
        //      * do nothing if we have an empty List/Set instead of display empty brackets like []
        if (value != null && !((value instanceof List || value instanceof Set) && ((Collection) value).isEmpty())) {
            if (value instanceof String) {
                cell = row.createCell(column, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) value);
            } else {
                if (value instanceof Integer) {
                    cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
                    cell.setCellValue((Integer) value);
                } else {
                    if (value instanceof BigDecimal) {
                        cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(((BigDecimal) value).doubleValue());
                    } else {
                        if (value instanceof Boolean) {
                            cell = row.createCell(column, Cell.CELL_TYPE_BOOLEAN);
                            cell.setCellValue(((Boolean) value) ? "Yes" : "No");
                        } else {
                            if (value instanceof Date) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                                cell = row.createCell(column, Cell.CELL_TYPE_STRING);
                                cell.setCellValue(sdf.format((Date) value));
                            } else {
                                cell = row.createCell(column, Cell.CELL_TYPE_STRING);
                                cell.setCellValue(value.toString());
                            }
                        }
                    }
                }
            }

            // determine the style of the row based on it's index
            if (row.getRowNum() < 1) {
                cell.setCellStyle(headerStyleCell);
            } else {
                cell.setCellStyle(dataStyleCell);
            }
        } else {
            // create a Cell.CELL_TYPE_BLANK
            row.createCell(column);
        }
    }

    /**
     * Creates a cell that is a link to another sheet in the document {@link Hyperlink#LINK_DOCUMENT}.
     *
     * @param value
     * @param row
     * @param column
     * @param sheetName
     * @param rowNumber
     */
    public void writeCellLink(final Object value, final Row row, final int column,
                              final String sheetName, final int rowNumber) {
        this.writeCell(value, row, column);

        Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
        // always point to first column A in excel file
        link.setAddress("'" + sheetName + "'!A" + rowNumber);
        cell.setHyperlink(link);

        cell.setCellStyle(linkStyleCell);
    }

    /**
     * Create a new row and set the default height (different heights for headers and data rows)
     *
     * @param sheet
     * @param rowNumber
     * @return Row
     */
    protected Row createRow(final Sheet sheet, final int rowNumber) {
        Row row = sheet.createRow(rowNumber);

        if (rowNumber < 1) {
            row.setHeight((short) 800);             // 40px (800 / 10 / 2)
        } else {
            row.setHeight((short) 600);             // 30px (600 / 10 / 2)
        }

        return row;
    }
}
