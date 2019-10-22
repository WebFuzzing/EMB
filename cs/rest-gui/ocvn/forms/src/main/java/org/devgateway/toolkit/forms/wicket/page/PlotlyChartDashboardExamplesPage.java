package org.devgateway.toolkit.forms.wicket.page;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devgateway.toolkit.forms.wicket.components.charts.Annotation;
import org.devgateway.toolkit.forms.wicket.components.charts.Data;
import org.devgateway.toolkit.forms.wicket.components.charts.Font;
import org.devgateway.toolkit.forms.wicket.components.charts.Layout;
import org.devgateway.toolkit.forms.wicket.components.charts.Line;
import org.devgateway.toolkit.forms.wicket.components.charts.Marker;
import org.devgateway.toolkit.forms.wicket.components.charts.PlotlyChart;
import org.devgateway.toolkit.forms.wicket.components.charts.Xaxis;
import org.devgateway.toolkit.forms.wicket.components.charts.Yaxis;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author idobre
 * @since 4/19/17
 */
@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_USER)
@MountPath(value = "/plotly-chart-dashboard-examples")
public class PlotlyChartDashboardExamplesPage extends BasePage {
    private static final Integer[] DATA1 = {19, 26, 55};

    private static final int SIZE1 = 12;
    private static final Integer[] DATA2 = {16, 15, 12, 6, 5, 4, 42};
    private static final Integer[] DATA3 = {12, 18, 29};

    private static final Integer[] DATA4 = {1, 4, 9, 16};
    private static final Integer[] DATA5 = {1, 2, 3, 4};
    private static final Number[] DATA6 = {6, -8, -4.5, 8};
    private static final Integer[] DATA7 = {1, 2, 3, 4};
    private static final Number[] DATA8 = {-15, -3, 4.5, -8};
    private static final Integer[] DATA9 = {1, 2, 3, 4};
    private static final Integer[] DATA10 = {-1, 3, -3, -4};
    private static final Integer[] DATA11 = {1, 2, 3, 4};

    private static final int WIDTH = 500;
    private static final int HEIGHT = 600;
    private static final double HOLE = .4;
    private static final double X = 0.5;
    private static final double Y = 0.5;
    private static final int SIZE = 20;
    private static final int TICKANGLE = -45;
    private static final int GRIDWIDTH = 2;
    private static final double BARGAP = 0.05;
    private static final double OPACITY = 0.6;
    private static final double WIDTH1 = 1.5;

    private static final Integer[] Y1 = {10, 15, 13, 17};
    private static final Integer[] X1 = {1, 2, 3, 4};
    private static final Integer[] Y2 = {16, 5, 11, 9};
    private static final Integer[] X2 = {1, 2, 3, 4};
    private static final Integer[] Y3 = {12, 9, 15, 12};
    private static final Integer[] X3 = {1, 2, 3, 4};
    private static final Integer[] Y4 = {10, 15, 13, 17};
    private static final Integer[] X4 = {1, 2, 3, 4};
    private static final Integer[] Y5 = {16, 5, 11, 9};
    private static final Integer[] X5 = {1, 2, 3, 4};
    private static final Integer[] Y6 = {0, 2, 3, 5};
    private static final Integer[] X6 = {1, 2, 3, 4};
    private static final Integer[] Y7 = {3, 5, 1, 7};
    private static final Integer[] X7 = {1, 2, 3, 4};

    private final Logger logger = LoggerFactory.getLogger(PlotlyChartDashboardExamplesPage.class);

    public PlotlyChartDashboardExamplesPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addPieCharts();
        addBarCharts();
        addLineCharts();
    }

    private void addPieCharts() {
        final Data pieChartData1 = new Data.DataBuilder()
                .setValues(new ArrayList<>(Arrays.asList(DATA1)))
                .setLabels(new ArrayList<>(Arrays.asList("Residential", "Non-Residential", "Utility")))
                .setType(PlotlyChart.CHART_TYPE_PIE)
                .build();
        final Layout pieChartLayout1 = new Layout.LayoutBuilder()
                .setWidth(WIDTH)
                .setHeight(HEIGHT)
                .build();

        final PlotlyChart pieChart1 = new PlotlyChart("pieChart1",
                new ArrayList<>(Arrays.asList(pieChartData1)), pieChartLayout1);
        add(pieChart1);

        /* ******************************************************************************************************  */

        final Data pieChartData2 = new Data.DataBuilder()
                .setValues(new ArrayList<>(Arrays.asList(DATA2)))
                .setLabels(new ArrayList<>(Arrays.asList("US", "China", "European Union", "Russian Federation",
                        "Brazil", "India", "Rest of World")))
                .setType(PlotlyChart.CHART_TYPE_PIE)
                .setName("GHG Emissions")
                .setHoverinfo(PlotlyChart.HOVERINFO_LABEL_PERCENT_NAME)
                .setHole(HOLE)
                .build();
        final Layout pieChartLayout2 = new Layout.LayoutBuilder()
                .setWidth(WIDTH)
                .setHeight(HEIGHT)
                .setTitle("Global Emissions 1990-2011")
                .setAnnotations(
                        new ArrayList<>(Arrays.asList(
                                new Annotation.AnnotationBuilder()
                                        .setFont(
                                                new Font.FontBuilder()
                                                        .setSize(SIZE)
                                                        .build())
                                        .setShowarrow(false)
                                        .setText("GHG")
                                        .setX(X)
                                        .setY(Y)
                                        .build()
                        )))
                .build();

        final PlotlyChart pieChart2 = new PlotlyChart("pieChart2",
                new ArrayList<>(Arrays.asList(pieChartData2)), pieChartLayout2);
        add(pieChart2);
    }

    private void addBarCharts() {
        final Data barChartData1 = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA1)))
                .setX(new ArrayList<>(Arrays.asList("giraffes", "orangutans", "monkeys")))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .build();

        final PlotlyChart barChart1 = new PlotlyChart("barChart1",
                new ArrayList<>(Arrays.asList(barChartData1)), null);
        add(barChart1);
        /* ******************************************************************************************************  */
        final Data barChartData2a = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA1)))
                .setX(new ArrayList<>(Arrays.asList("giraffes", "orangutans", "monkeys")))
                .setName("SF Zoo")
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .build();

        final Data barChartData2b = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA3)))
                .setX(new ArrayList<>(Arrays.asList("giraffes", "orangutans", "monkeys")))
                .setName("LA Zoo")
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .build();

        final Layout barChartLayout2 = new Layout.LayoutBuilder()
                .setBarmode(PlotlyChart.BARMODE_GROUP)
                .build();

        final PlotlyChart barChart2 = new PlotlyChart("barChart2",
                new ArrayList<>(Arrays.asList(barChartData2a, barChartData2b)), barChartLayout2);
        add(barChart2);
        /* ******************************************************************************************************  */
        final Layout barChartLayout3 = new Layout.LayoutBuilder()
                .setBarmode(PlotlyChart.BARMODE_STACK)
                .build();

        final PlotlyChart barChart3 = new PlotlyChart("barChart3",
                new ArrayList<>(Arrays.asList(barChartData2a, barChartData2b)), barChartLayout3);
        add(barChart3);

        /* ******************************************************************************************************  */
        final Data barChartData4 = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA2)))
                .setX(new ArrayList<>(Arrays.asList("Liam", "Sophie", "Jacob", "Mia", "William", "Olivia")))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .setText(new ArrayList<>(Arrays.asList("4.17 below the mean", "4.17 below the mean",
                        "0.17 below the mean", "0.17 below the mean", "0.83 above the mean", "7.83 above the mean")))
                .setMarker(new Marker.MarkerBuilder()
                        .setColor("rgb(142,124,195)")
                        .build())
                .build();

        final Layout barChartLayout4 = new Layout.LayoutBuilder()
                .setTitle("Number of Graphs Made this Week")
                .setFont(new Font.FontBuilder()
                        .setFamily("Raleway, snas-serif")
                        .build())
                .setShowlegend(false)
                .setXaxis(new Xaxis.XaxisBuilder()
                        .setTickangle(TICKANGLE)
                        .build())
                .setYaxis(new Yaxis.YaxisBuilder()
                        .setZeroline(false)
                        .setGridwidth(GRIDWIDTH)
                        .build())
                .setBargap(BARGAP)
                .build();

        final PlotlyChart barChart4 = new PlotlyChart("barChart4",
                new ArrayList<>(Arrays.asList(barChartData4)), barChartLayout4);
        add(barChart4);
        /* ******************************************************************************************************  */
        final Data barChartData5 = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA1)))
                .setX(new ArrayList<>(Arrays.asList("Product A", "Product B", "Product C")))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .setText(new ArrayList<>(Arrays.asList("27% market share", "24% market share", "19% market share")))
                .setMarker(new Marker.MarkerBuilder()
                        .setColor("rgb(158,202,225)")
                        .setOpacity(OPACITY)
                        .setLine(new Line.LineBuilder()
                                .setColor("rbg(8,48,107)")
                                .setWidth(WIDTH1)
                                .build())
                        .build())
                .build();

        final Layout barChartLayout5 = new Layout.LayoutBuilder()
                .setTitle("Number of Graphs Made this Week")
                .setFont(new Font.FontBuilder()
                        .setFamily("Raleway, snas-serif")
                        .build())
                .setShowlegend(false)
                .setXaxis(new Xaxis.XaxisBuilder()
                        .setTickangle(TICKANGLE)
                        .build())
                .setYaxis(new Yaxis.YaxisBuilder()
                        .setZeroline(false)
                        .setGridwidth(GRIDWIDTH)
                        .build())
                .setBargap(BARGAP)
                .build();

        final PlotlyChart barChart5 = new PlotlyChart("barChart5",
                new ArrayList<>(Arrays.asList(barChartData5)), barChartLayout5);
        add(barChart5);

        /* ******************************************************************************************************  */
        final Data barChartData6a = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA4)))
                .setX(new ArrayList<>(Arrays.asList(DATA5)))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .setName("Trace1")
                .build();
        final Data barChartData6b = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA6)))
                .setX(new ArrayList<>(Arrays.asList(DATA7)))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .setName("Trace2")
                .build();
        final Data barChartData6c = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA8)))
                .setX(new ArrayList<>(Arrays.asList(DATA9)))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .setName("Trace3")
                .build();
        final Data barChartData6d = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(DATA10)))
                .setX(new ArrayList<>(Arrays.asList(DATA11)))
                .setType(PlotlyChart.CHART_TYPE_BAR)
                .setName("Trace4")
                .build();

        final Layout barChartLayout6 = new Layout.LayoutBuilder()
                .setTitle("Relative Barmode")
                .setBarmode(PlotlyChart.BARMODE_RELATIVE)
                .setXaxis(new Xaxis.XaxisBuilder()
                        .setTitle("X axis")
                        .build())
                .setYaxis(new Yaxis.YaxisBuilder()
                        .setTitle("Y axis")
                        .build())
                .build();

        final PlotlyChart barChart6 = new PlotlyChart("barChart6",
                new ArrayList<>(Arrays.asList(barChartData6a, barChartData6b, barChartData6c, barChartData6d)),
                barChartLayout6);
        add(barChart6);
    }

    private void addLineCharts() {
        final Data lineChartData1a = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y1)))
                .setX(new ArrayList<>(Arrays.asList(X1)))
                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setMode(PlotlyChart.MODE_MARKERS)
                .build();
        final Data lineChartData1b = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y2)))
                .setX(new ArrayList<>(Arrays.asList(X2)))
                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setMode(PlotlyChart.MODE_LINES)
                .build();
        final Data lineChartData1c = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y3)))
                .setX(new ArrayList<>(Arrays.asList(X3)))
                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setMode(PlotlyChart.MODE_LINES_MARKERS)
                .build();

        final PlotlyChart lineChart1 = new PlotlyChart("lineChart1",
                new ArrayList<>(Arrays.asList(lineChartData1a, lineChartData1b, lineChartData1c)), null);
        add(lineChart1);


        /* ******************************************************************************************************  */

        final Data lineChartData2a = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y4)))
                .setX(new ArrayList<>(Arrays.asList(X4)))
                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setMode(PlotlyChart.MODE_MARKERS)
                .setName("Team A")
                .setText(new ArrayList<>(Arrays.asList("A-1", "A-2", "A-3", "A-4", "A-5")))
                .setMarker(new Marker.MarkerBuilder()
                        .setSize(SIZE1)
                        .build())
                .build();
        final Data lineChartData2b = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y5)))
                .setX(new ArrayList<>(Arrays.asList(X5)))

                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setMode(PlotlyChart.MODE_MARKERS)
                .setName("Team B")
                .setText(new ArrayList<>(Arrays.asList("B-1", "B-2", "B-3", "B-4", "B-5")))
                .setMarker(new Marker.MarkerBuilder()
                        .setSize(SIZE1)
                        .build())
                .build();

        final Layout lineChartLayout2 = new Layout.LayoutBuilder()
                .setTitle("Data Labels Hover")
                .build();

        final PlotlyChart lineChart2 = new PlotlyChart("lineChart2",
                new ArrayList<>(Arrays.asList(lineChartData2a, lineChartData2b)), lineChartLayout2);
        add(lineChart2);


        /* ******************************************************************************************************  */

        final Data areaChartData1a = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y6)))
                .setX(new ArrayList<>(Arrays.asList(X6)))
                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setFill(PlotlyChart.FILL_TOZEROY)
                .build();
        final Data areaChartData1b = new Data.DataBuilder()
                .setY(new ArrayList<>(Arrays.asList(Y7)))
                .setX(new ArrayList<>(Arrays.asList(X7)))
                .setType(PlotlyChart.CHART_TYPE_SCATTER)
                .setFill(PlotlyChart.FILL_TONEXTY)
                .build();

        final Layout areaChartLayout1 = new Layout.LayoutBuilder()
                .setTitle("Area chart")
                .build();

        final PlotlyChart areaChart1 = new PlotlyChart("areaChart1",
                new ArrayList<>(Arrays.asList(areaChartData1a, areaChartData1b)), areaChartLayout1);
        add(areaChart1);
    }
}
