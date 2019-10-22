/**
 * init function that is called from the Wicket Component
 */
var init = function (parameters) {
    'use strict';

    var chart = new PlotlyChart(parameters);
    chart.render();
};

/**
 * Object.assign for ES5
 */
if (typeof Object.assign != 'function') {
    Object.assign = function(target) {
        'use strict';
        if (target == null) {
            throw new TypeError('Cannot convert undefined or null to object');
        }

        target = Object(target);
        for (var index = 1; index < arguments.length; index++) {
            var source = arguments[index];
            if (source != null) {
                for (var key in source) {
                    if (Object.prototype.hasOwnProperty.call(source, key)) {
                        target[key] = source[key];
                    }
                }
            }
        }
        return target;
    };
}

/**
 * Use this if you want to configure some default properties for a plot.ly chart.
 */
PlotlyChart.prototype.defaultProps = {
    chartId:          'chartId',
    layout: {
        showlegend:    true,
    }
};

/**
 * Use this constructor in order to initialize a PlotlyChart.
 * If you want to render the chart use the `render()` function.
 */
function PlotlyChart(parameters) {
    this.props = Object.assign({}, this.defaultProps, parameters);
}

/**
 * Function that actually renders the chart using plot.ly
 */
PlotlyChart.prototype.render = function() {
    var chartId = this.props.chartId;
    var data = this.props.data;
    var layout = this.props.layout;

    // console.log(JSON.stringify(this.props, null, '\t'));

    Plotly.newPlot(chartId, data, layout, {displayModeBar: true, displaylogo: false});
};
