package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.devgateway.ocds.web.rest.controller.CostEffectivenessVisualsController;
import org.devgateway.ocds.web.rest.controller.request.LangGroupingFilterPagingRequest;
import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author idobre
 * @since 8/19/16
 *
 * Exports an excel chart based on *Cost effectiveness* dashboard
 */
@RestController
public class CostEffectivenessExcelController extends ExcelChartOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private CostEffectivenessVisualsController costEffectivenessVisualsController;

    @ApiOperation(value = "Exports *Cost effectiveness* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/costEffectivenessExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void costEffectivenessExcelChart(@ModelAttribute @Valid final LangGroupingFilterPagingRequest filter,
                                            final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(),
                "charts:costEffectiveness:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> costEffectivenessTenderAwardAmount =
                costEffectivenessVisualsController.costEffectivenessTenderAwardAmount(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                costEffectivenessTenderAwardAmount);

        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> tenderPrice = excelChartHelper.getValuesFromDBObject(costEffectivenessTenderAwardAmount,
                categories, getExportYearMonthXAxis(filter),
                CostEffectivenessVisualsController.Keys.TOTAL_TENDER_AMOUNT);
        final List<Number> diffPrice = excelChartHelper.getValuesFromDBObject(costEffectivenessTenderAwardAmount,
                categories,  getExportYearMonthXAxis(filter),
                CostEffectivenessVisualsController.Keys.DIFF_TENDER_AWARD_AMOUNT);
        // use trillions for amounts
        for (int i = 0; i < tenderPrice.size(); i++) {
            if (tenderPrice.get(i) != null) {
                tenderPrice.set(i, tenderPrice.get(i).doubleValue() / 1000000000);
            }
            if (diffPrice.get(i) != null) {
                diffPrice.set(i, diffPrice.get(i).doubleValue() / 1000000000);
            }
        }
        if (!tenderPrice.isEmpty()) {
            values.add(tenderPrice);
        }
        if (!diffPrice.isEmpty()) {
            values.add(diffPrice);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(),
                            "charts:costEffectiveness:traces:tenderPrice"),
                    translationService.getValue(filter.getLanguage(), "charts:costEffectiveness:traces:awardPrice")
            );
        } else {
            seriesTitle = new ArrayList<>();
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");

        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.stackedcol,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
