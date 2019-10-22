package org.devgateway.toolkit.forms.wicket.components.charts;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * POJO used to pass the chart parameters.
 * This is going to be transformed into a JSON and passed to the JS init function of the entity being built.
 *
 * For all options check: https://plot.ly/javascript/reference/
 *
 * @author idobre
 * @since 3/6/17
 */
public final class ChartParameters implements Serializable {
    private final String chartId;

    private final List<Data> data;

    private final Layout layout;

    public ChartParameters(final String chartId, final List<Data> data, final Layout layout) {
        this.chartId = chartId;
        this.data = data;
        this.layout = layout;
    }

    public String toJson() {
        Gson gson = new Gson();
        String ret = gson.toJson(this);

        return ret;
    }
}
