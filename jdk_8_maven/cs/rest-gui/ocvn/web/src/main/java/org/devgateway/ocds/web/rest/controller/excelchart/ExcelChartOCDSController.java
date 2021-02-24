package org.devgateway.ocds.web.rest.controller.excelchart;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.spring.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mpostelnicu on 2/17/17.
 */
public abstract class ExcelChartOCDSController extends GenericOCDSController {

    @Autowired
    protected TranslationService translationService;

    /**
     * This is used to define the X axis based on yearly or monthly value is present.
     *
     * @param filter
     * @return
     */
    protected String getExportYearMonthXAxis(YearFilterPagingRequest filter) {
        return filter.getMonthly() ? "month" : "year";
    }

}
