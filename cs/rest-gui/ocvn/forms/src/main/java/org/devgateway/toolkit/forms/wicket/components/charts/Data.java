package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;
import java.util.List;

/**
 * @author idobre
 * @since 4/19/17
 */
public final class Data implements Serializable {
    private final List<? extends Number> values;

    private final List<String> labels;

    private final List<?> x;

    private final List<? extends Number> y;

    private final String type;

    private final String name;

    private final String hoverinfo;

    private final Double hole;

    private final Double pull;

    private final List<String> text;

    private final Marker marker;

    private final String mode;

    private final String fill;

    public Data(final DataBuilder dataBuilder) {
        this.values = dataBuilder.values;
        this.labels = dataBuilder.labels;
        this.x = dataBuilder.x;
        this.y = dataBuilder.y;
        this.type = dataBuilder.type;
        this.name = dataBuilder.name;
        this.hoverinfo = dataBuilder.hoverinfo;
        this.hole = dataBuilder.hole;
        this.pull = dataBuilder.pull;
        this.text = dataBuilder.text;
        this.marker = dataBuilder.marker;
        this.mode = dataBuilder.mode;
        this.fill = dataBuilder.fill;
    }

    public static class DataBuilder {
        private List<? extends Number> values;

        private List<String> labels;

        private List<?> x;

        private List<? extends Number> y;

        private String type;

        private String name;

        private String hoverinfo;

        private Double hole;

        private Double pull;

        private List<String> text;

        private Marker marker;

        private String mode;

        private String fill;

        public DataBuilder setValues(final List<? extends Number> values) {
            this.values = values;
            return this;
        }

        public DataBuilder setLabels(final List<String> labels) {
            this.labels = labels;
            return this;
        }

        public DataBuilder setX(final List<?> x) {
            this.x = x;
            return this;
        }

        public DataBuilder setY(final List<? extends Number> y) {
            this.y = y;
            return this;
        }

        public DataBuilder setType(final String type) {
            this.type = type;
            return this;
        }

        public DataBuilder setName(final String name) {
            this.name = name;
            return this;
        }

        public DataBuilder setHoverinfo(final String hoverinfo) {
            this.hoverinfo = hoverinfo;
            return this;
        }

        public DataBuilder setHole(final Double hole) {
            this.hole = hole;
            return this;
        }

        public DataBuilder setPull(final Double pull) {
            this.pull = pull;
            return this;
        }

        public DataBuilder setText(final List<String> text) {
            this.text = text;
            return this;
        }

        public DataBuilder setMarker(final Marker marker) {
            this.marker = marker;
            return this;
        }

        public DataBuilder setMode(final String mode) {
            this.mode = mode;
            return this;
        }

        public DataBuilder setFill(final String fill) {
            this.fill = fill;
            return this;
        }

        public Data build() {
            return new Data(this);
        }
    }
}
