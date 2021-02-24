package org.devgateway.ocds.web.rest.controller.export;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileCleaningTracker;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.util.SettingsUtils;
import org.devgateway.toolkit.persistence.repository.AdminSettingsRepository;
import org.devgateway.toolkit.web.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author idobre
 * @since 7/23/16
 */
@RestController
public class ExcelExportController extends GenericOCDSController {
    protected final Logger logger = LoggerFactory.getLogger(ExcelExportController.class);
    @Autowired
    protected AdminSettingsRepository adminSettingsRepository;
    @Autowired
    private SettingsUtils settingsUtils;
    @Autowired
    private ExcelGenerator excelGenerator;
    @Autowired
    private FileCleaningTracker fileCleaningTracker;

    @ApiOperation(value = "Export releases in Excel format.")
    @RequestMapping(value = "/api/ocds/excelExport", method = {RequestMethod.GET, RequestMethod.POST})
    public void excelExport(@ModelAttribute @Valid final YearFilterPagingRequest filter,
                            final HttpServletResponse response) throws IOException {


        Boolean disabledSecurity = SecurityUtil.getDisabledApiSecurity(adminSettingsRepository);
        if (disabledSecurity) {
            return;
        }

        // set the default page size from admin settings
        filter.setPageSize(settingsUtils.getExcelBatchSize());

        long numberOfReleases = mongoTemplate
                .count(query(getYearDefaultFilterCriteria(filter, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE)),
                        Release.class);

        // if we need to export just one file then we don't create an archive
        if (numberOfReleases <= settingsUtils.getExcelBatchSize()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + "excel-export.xlsx");
            response.getOutputStream().write(excelGenerator.getExcelDownload(filter));
        } else {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + "excel-export.zip");
            response.flushBuffer();
            ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zout.setMethod(ZipOutputStream.DEFLATED);
            zout.setLevel(Deflater.NO_COMPRESSION);
            int numberOfPages = (int) Math.ceil((double) numberOfReleases / filter.getPageSize());
            for (int i = 0; i < numberOfPages; i++) {
                filter.setPageNumber(i);
                ZipEntry ze = new ZipEntry("excel-export-page " + (i + 1) + ".xlsx");
                zout.putNextEntry(ze);
                byte[] bytes = excelGenerator.getExcelDownload(filter);
                zout.write(bytes, 0, bytes.length);
                zout.closeEntry();
                response.flushBuffer();
            }
            zout.close();
            response.flushBuffer();
        }
    }
}
