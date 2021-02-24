package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;
import java.util.List;

/**
 * @author idobre
 * @since 4/21/17
 */
public class Marker implements Serializable {
    private final String color;

    private final List<String> colors;

    private final Double opacity;

    private final Integer size;

    private final Line line;

    public Marker(final MarkerBuilder markerBuilder) {
        this.color = markerBuilder.color;
        this.colors = markerBuilder.colors;
        this.opacity = markerBuilder.opacity;
        this.size = markerBuilder.size;
        this.line = markerBuilder.line;
    }

    public static class MarkerBuilder {
        private String color;

        private List<String> colors;

        private Double opacity;

        private Integer size;

        private Line line;

        public MarkerBuilder setColor(final String color) {
            this.color = color;
            return this;
        }

        public MarkerBuilder setColors(final List<String> colors) {
            this.colors = colors;
            return this;
        }

        public MarkerBuilder setOpacity(final Double opacity) {
            this.opacity = opacity;
            return this;
        }

        public MarkerBuilder setSize(final Integer size) {
            this.size = size;
            return this;
        }

        public MarkerBuilder setLine(final Line line) {
            this.line = line;
            return this;
        }

        public Marker build() {
            return new Marker(this);
        }
    }
}
