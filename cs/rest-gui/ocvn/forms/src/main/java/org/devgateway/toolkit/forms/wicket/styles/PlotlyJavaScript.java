package org.devgateway.toolkit.forms.wicket.styles;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author idobre
 * @since 3/6/17
 */
public class PlotlyJavaScript extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    public static final PlotlyJavaScript INSTANCE = new PlotlyJavaScript();

    /**
     * Construct.
     */
    public PlotlyJavaScript() {
        super(PlotlyJavaScript.class, "/assets/js/plotly/plotly-basic.min.js");
    }
}
