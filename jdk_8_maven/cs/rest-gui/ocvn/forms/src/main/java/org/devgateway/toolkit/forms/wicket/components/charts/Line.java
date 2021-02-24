package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;

/**
 * @author idobre
 * @since 4/21/17
 */
public class Line implements Serializable {
    private final String color;

    private final Double width;

    public Line(final LineBuilder lineBuilder) {
        this.color = lineBuilder.color;
        this.width = lineBuilder.width;
    }

    public static class LineBuilder {
        private String color;

        private Double width;

        public LineBuilder setColor(final String color) {
            this.color = color;
            return this;
        }

        public LineBuilder setWidth(final Double width) {
            this.width = width;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
