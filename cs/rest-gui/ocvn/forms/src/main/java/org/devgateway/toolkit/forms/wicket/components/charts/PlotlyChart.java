package org.devgateway.toolkit.forms.wicket.components.charts;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.devgateway.toolkit.forms.wicket.styles.PlotlyJavaScript;

import java.util.List;

/**
 * @author idobre
 * @since 4/19/17
 */
public final class PlotlyChart extends Panel {
    private static final String JS_FILE = "plotlyChart.js";
    private static final String INIT_FUNCTION = "init";

    public static final String CHART_TYPE_PIE = "pie";
    public static final String CHART_TYPE_BAR = "bar";
    public static final String CHART_TYPE_SCATTER = "scatter";

    public static final String HOVERINFO_LABEL_PERCENT_NAME = "label+percent+name";

    public static final String BARMODE_GROUP = "group";
    public static final String BARMODE_STACK = "stack";
    public static final String BARMODE_RELATIVE = "relative";

    public static final String MODE_MARKERS = "markers";
    public static final String MODE_LINES = "lines";
    public static final String MODE_LINES_MARKERS = "lines+markers";

    public static final String FILL_TOZEROY = "tozeroy";
    public static final String FILL_TONEXTY = "tonexty";

    public static final String AXIS_TYPE_CATEGORY = "category";

    private final WebMarkupContainer chart;

    private final ChartParameters parameters;

    public PlotlyChart(final String id, final List<Data> data, final Layout layout) {
        super(id);

        this.chart = new WebMarkupContainer("chart");
        this.chart.setOutputMarkupId(true);
        add(this.chart);

        this.parameters = new ChartParameters(this.chart.getMarkupId(), data, layout);
    }


    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);

        // plot.ly library
        response.render(JavaScriptHeaderItem.forReference(PlotlyJavaScript.INSTANCE));

        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(
                ChartParameters.class, JS_FILE)));
        response.render(OnDomReadyHeaderItem.forScript(INIT_FUNCTION + "(" + parameters.toJson() + ");"));
    }
}
