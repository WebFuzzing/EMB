package org.devgateway.ocds.web.rest.controller.export;

import org.apache.poi.ss.usermodel.Workbook;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.excel.ExcelFile;
import org.devgateway.ocds.persistence.mongo.excel.ReleaseExportFile;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author idobre
 * @since 7/29/16
 */
@Service
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "excelExport")
public class ExcelGenerator extends GenericOCDSController {
    protected final Logger logger = LoggerFactory.getLogger(ExcelGenerator.class);

    /**
     * Method that returns a byte array with excel export.
     *
     * @param filter
     * @return
     * @throws IOException
     */
    @Cacheable
    public byte[] getExcelDownload(final YearFilterPagingRequest filter) throws IOException {
        PageRequest pageRequest = new PageRequest(filter.getPageNumber(), filter.getPageSize(),
                Sort.Direction.ASC, "id");

        List<Release> releases = mongoTemplate.find(
                query(getYearDefaultFilterCriteria(filter,
                        MongoConstants.FieldNames.TENDER_PERIOD_START_DATE)).with(pageRequest),
                Release.class);

        ExcelFile releaseExcelFile = new ReleaseExportFile(releases);
        Workbook workbook = releaseExcelFile.createWorkbook();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        byte[] bytes = baos.toByteArray();

        return bytes;
    }
}
