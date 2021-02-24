package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.devgateway.ocds.web.rest.controller.CountPlansTendersAwardsController;
import org.devgateway.ocds.web.rest.controller.request.LangYearFilterPagingRequest;
import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author idobre
 * @since 8/17/16
 * <p>
 * Exports an excel chart based on *Procurement activity by year* dashboard
 */
@RestController
public class ProcurementActivityByYearController extends ExcelChartOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private CountPlansTendersAwardsController countPlansTendersAwardsController;

    @ApiOperation(value = "Exports *Procurement activity by year* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/procurementActivityExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void procurementActivityExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                              final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(), "charts:overview:title");

        // fetch the data that will be displayed in the chart (we have multiple sources for this dashboard)
        final List<DBObject> countAwardsByYear = countPlansTendersAwardsController.countAwardsByYear(filter);
        final List<DBObject> countTendersByYear = countPlansTendersAwardsController.countTendersByYear(filter);
        final List<DBObject> countBidPlansByYear = countPlansTendersAwardsController.countBidPlansByYear(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(
                getExportYearMonthXAxis(filter),
                countAwardsByYear, countTendersByYear, countBidPlansByYear);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> valueAwards = excelChartHelper.getValuesFromDBObject(countAwardsByYear, categories,
                getExportYearMonthXAxis(filter), CountPlansTendersAwardsController.Keys.COUNT);
        final List<Number> valueTenders = excelChartHelper.getValuesFromDBObject(countTendersByYear, categories,
                getExportYearMonthXAxis(filter), CountPlansTendersAwardsController.Keys.COUNT);
        final List<Number> valueBidPlans = excelChartHelper.getValuesFromDBObject(countBidPlansByYear, categories,
                getExportYearMonthXAxis(filter), CountPlansTendersAwardsController.Keys.COUNT);
        if (!valueAwards.isEmpty()) {
            values.add(valueAwards);
        }
        if (!valueTenders.isEmpty()) {
            values.add(valueTenders);
        }
        if (!valueBidPlans.isEmpty()) {
            values.add(valueBidPlans);
        }


        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:overview:traces:award"),
                    translationService.getValue(filter.getLanguage(), "charts:overview:traces:tender"),
                    translationService.getValue(filter.getLanguage(), "charts:overview:traces:bidplan"));
        } else {
            seriesTitle = new ArrayList<>();
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.line,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
