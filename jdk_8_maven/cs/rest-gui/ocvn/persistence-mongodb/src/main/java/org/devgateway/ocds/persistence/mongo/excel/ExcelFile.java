package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Create an Workbook that can be exported
 *
 * @author idobre
 * @since 6/7/16
 */
public interface ExcelFile {
    Workbook createWorkbook();
}
