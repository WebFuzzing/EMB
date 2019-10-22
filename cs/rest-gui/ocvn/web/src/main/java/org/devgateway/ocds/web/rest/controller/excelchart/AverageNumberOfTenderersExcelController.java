package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.devgateway.ocds.web.rest.controller.AverageNumberOfTenderersController;
import org.devgateway.ocds.web.rest.controller.request.LangYearFilterPagingRequest;
import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author idobre
 * @since 8/22/16
 * <p>
 * Exports an excel chart based on *Average number of bids* dashboard
 */
@RestController
public class AverageNumberOfTenderersExcelController extends ExcelChartOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private AverageNumberOfTenderersController averageNumberOfTenderersController;

    @ApiOperation(value = "Exports *Average number of bids* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/averageNumberBidsExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void averageNumberBidsExcelChart(@ModelAttribute @Valid final LangYearFilterPagingRequest filter,
                                            final HttpServletResponse response) throws IOException {
        final String chartTitle = translationService.getValue(filter.getLanguage(), "charts:avgNrBids:title");

        // fetch the data that will be displayed in the chart
        final List<DBObject> averageNumberOfTenderers =
                averageNumberOfTenderersController.averageNumberOfTenderers(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(
                getExportYearMonthXAxis(filter), averageNumberOfTenderers);

        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> totalTenderAmount = excelChartHelper.getValuesFromDBObject(averageNumberOfTenderers,
                categories, getExportYearMonthXAxis(filter),
                AverageNumberOfTenderersController.Keys.AVERAGE_NO_OF_TENDERERS);
        if (!totalTenderAmount.isEmpty()) {
            values.add(totalTenderAmount);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    translationService.getValue(filter.getLanguage(), "charts:avgNrBids:yAxisTitle"));
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
