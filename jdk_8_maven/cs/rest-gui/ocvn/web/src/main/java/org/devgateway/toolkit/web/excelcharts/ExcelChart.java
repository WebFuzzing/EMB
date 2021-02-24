package org.devgateway.toolkit.web.excelcharts;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author idobre
 * @since 8/16/16
 *
 *        Create an Workbook with an excel chart that can be exported.
 */
public interface ExcelChart {
    Workbook createWorkbook();

    void configureSeriesTitle(List<String> seriesTitle);
}
