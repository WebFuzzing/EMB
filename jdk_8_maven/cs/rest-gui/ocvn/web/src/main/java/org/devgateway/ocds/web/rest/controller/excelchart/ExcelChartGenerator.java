package org.devgateway.ocds.web.rest.controller.excelchart;

import org.apache.poi.ss.usermodel.Workbook;
import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.devgateway.toolkit.web.excelcharts.ExcelChart;
import org.devgateway.toolkit.web.excelcharts.ExcelChartDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author idobre
 * @since 8/17/16
 */
@Service
@CacheConfig(keyGenerator = "genericExcelChartKeyGenerator", cacheNames = "excelChartExport")
public class ExcelChartGenerator {
    private final Logger logger = LoggerFactory.getLogger(ExcelChartGenerator.class);

    /**
     * Generate an Excel Chart based on (categories, values)
     */
    @Cacheable
    public byte[] getExcelChart(final ChartType type,
                                final String title,
                                final List<String> seriesTitle,
                                final List<?> categories,
                                final List<List<? extends Number>> values) throws IOException {
        final ExcelChart excelChart = new ExcelChartDefault(title, type, categories, values);
        excelChart.configureSeriesTitle(seriesTitle);
        final Workbook workbook = excelChart.createWorkbook();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);

        return baos.toByteArray();
    }
}
