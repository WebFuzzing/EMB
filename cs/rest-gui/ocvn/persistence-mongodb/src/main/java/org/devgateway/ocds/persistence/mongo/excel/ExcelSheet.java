package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.ss.usermodel.Row;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExportSepareteSheet;

import java.util.List;

/**
 * Create an Excel Sheet representation of an Object of type T.
 * In this process it's possible to create new Excel Sheet if
 * some fields are annotated with {@link ExcelExportSepareteSheet}
 *
 * @author idobre
 * @since 6/7/16
 */
public interface ExcelSheet {
    void writeCell(Object value, Row row, int column);

    void writeRow(Object object, Row row);

    void writeSheet(List<Object> objects);

    void emptySheet();
}
