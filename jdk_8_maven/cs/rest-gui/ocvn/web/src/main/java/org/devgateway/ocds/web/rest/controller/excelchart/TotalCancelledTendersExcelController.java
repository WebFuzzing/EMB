package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.devgateway.ocds.web.rest.controller.TotalCancelledTendersByYearController;
import org.devgateway.ocds.web.rest.controller.request.LangYearFilterPagingRequest;
import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author idobre
 * @since 8/23/16
 *
 * Exports an excel chart based on *Cancelled funding* dashboard
 */
@RestController
public class TotalCancelledTendersExcelController extends ExcelChartOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private TotalCancelledTendersByYearController totalCancelledTendersByYearController;

    @ApiOperation(value = "Exports *Cancelled funding* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/cancelledFundingExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void cancelledFundingExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                           final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(),
                "charts:cancelledAmounts:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = totalCancelledTendersByYearController
                .totalCancelledTendersByYear(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> cancelledAmount = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                getExportYearMonthXAxis(filter),
                TotalCancelledTendersByYearController.Keys.TOTAL_CANCELLED_TENDERS_AMOUNT);
        // use trillions for amounts
        for (int i = 0; i < cancelledAmount.size(); i++) {
            if (cancelledAmount.get(i) != null) {
                cancelledAmount.set(i, cancelledAmount.get(i).doubleValue() / 1000000000);
            }
        }
        if (!cancelledAmount.isEmpty()) {
            values.add(cancelledAmount);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:cancelledAmounts:yAxisName"));
        } else {
            seriesTitle = new ArrayList<>();
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.area,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }


    @ApiOperation(value = "Exports *Cancelled funding by reason* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/cancelledTendersByYearByRationaleExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void cancelledTendersByYearByRationaleExcelChart(@ModelAttribute @Valid
                                                            final LangYearFilterPagingRequest filter,
                                                            final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(),
                "charts:cancelledFunding:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = totalCancelledTendersByYearController
                .totalCancelledTendersByYearByRationale(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(
                getExportYearMonthXAxis(filter), totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> cancelledAmount = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                getExportYearMonthXAxis(filter),
                TotalCancelledTendersByYearController.Keys.TOTAL_CANCELLED_TENDERS_AMOUNT);
        // use trillions for amounts
        for (int i = 0; i < cancelledAmount.size(); i++) {
            if (cancelledAmount.get(i) != null) {
                cancelledAmount.set(i, cancelledAmount.get(i).doubleValue() / 1000000000);
            }
        }
        if (!cancelledAmount.isEmpty()) {
            values.add(cancelledAmount);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:cancelledFunding:yAxisTitle"));
        } else {
            seriesTitle = new ArrayList<>();
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.barcol,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
