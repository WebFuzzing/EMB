package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.devgateway.ocds.persistence.mongo.Release;

import java.util.ArrayList;
import java.util.List;

/**
 * @author idobre
 * @since 6/7/16
 */
public final class ReleaseExportFile implements ExcelFile {
    private final List<Release> releases;

    private final Workbook workbook;

    public ReleaseExportFile(final List<Release> releases) {
        this.releases = releases;

        // create the excel file
        this.workbook = new SXSSFWorkbook(100);
    }

    @Override
    public Workbook createWorkbook() {
        ExcelSheet releaseSheet = new OCDSObjectExcelSheet(this.workbook, Release.class);

        // don't do anything if the list of releases is empty, just display the error message
        if (releases != null && !releases.isEmpty()) {
            releaseSheet.writeSheet(new ArrayList<>(releases));
        }

        return workbook;
    }
}
