package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.devgateway.ocds.web.rest.controller.TenderPercentagesController;
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
 * Exports an excel chart based on *Cancelled funding (percentage)* dashboard
 */
@RestController
public class TenderPercentagesExcelController extends ExcelChartOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private TenderPercentagesController tenderPercentagesController;

    @ApiOperation(value = "Exports *Cancelled funding (percentage)* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/cancelledFundingPercentageExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void cancelledFundingPercentageExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                                     final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(),
                "charts:cancelledPercents:title");
        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = tenderPercentagesController.percentTendersCancelled(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> percentCancelled = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                getExportYearMonthXAxis(filter), TenderPercentagesController.Keys.PERCENT_CANCELLED);
        if (!percentCancelled.isEmpty()) {
            values.add(percentCancelled);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:cancelledPercents:yAxisName"));
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

    @ApiOperation(value = "Exports *Number of cancelled bids* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/numberCancelledFundingExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void numberCancelledFundingExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                                 final HttpServletResponse response) throws IOException {

        final String chartTitle = translationService.getValue(filter.getLanguage(),
                "charts:cancelledPercents:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = tenderPercentagesController.percentTendersCancelled(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> totalCancelled = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                getExportYearMonthXAxis(filter), TenderPercentagesController.Keys.TOTAL_CANCELLED);
        if (!totalCancelled.isEmpty()) {
            values.add(totalCancelled);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(),
                    "charts:cancelledPercents:yAxisName"));
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

    @ApiOperation(value = "Exports *Percent of Tenders Using e-Bid* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/percentTendersUsingEBidExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void percentTendersUsingEBidExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                                  final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(), "charts:percentEBid:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = tenderPercentagesController.percentTendersUsingEBid(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> percentUsingEBid = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                getExportYearMonthXAxis(filter), TenderPercentagesController.Keys.PERCENTAGE_TENDERS_USING_EBID);
        if (!percentUsingEBid.isEmpty()) {
            values.add(percentUsingEBid);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:percentEBid:yAxisName"));
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

    @ApiOperation(value = "Exports *Number of eBid Awards* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/numberTendersUsingEBidExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void numberTendersUsingEBidExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                                 final HttpServletResponse response) throws IOException {

        final String chartTitle = translationService.getValue(filter.getLanguage(), "charts:nrEBid:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = tenderPercentagesController.percentTendersUsingEBid(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> countUsingEBid = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                getExportYearMonthXAxis(filter), TenderPercentagesController.Keys.TOTAL_TENDERS_USING_EBID);
        if (!countUsingEBid.isEmpty()) {
            values.add(countUsingEBid);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:nrEBid:yAxisTitle"));
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

    @ApiOperation(value = "Exports *Percentage of plans with tender* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/tendersWithLinkedProcurementPlanExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void tendersWithLinkedProcurementPlanExcelChart(@ModelAttribute @Valid
                                                               final LangYearFilterPagingRequest filter,
                                                           final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(),
                "charts:percentWithTenders:title");



        // fetch the data that will be displayed in the chart
        final List<DBObject> percentTendersWithLinkedProcurementPlan = tenderPercentagesController
                .percentTendersWithLinkedProcurementPlan(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(getExportYearMonthXAxis(filter),
                percentTendersWithLinkedProcurementPlan);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> percentTenders = excelChartHelper.getValuesFromDBObject(
                percentTendersWithLinkedProcurementPlan, categories, getExportYearMonthXAxis(filter),
                TenderPercentagesController.Keys.PERCENT_TENDERS);
        values.add(percentTenders);

        final List<String> seriesTitle = Arrays.asList(
                translationService.getValue(filter.getLanguage(),
                        "charts:percentWithTenders:yAxisTitle"));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.area,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
