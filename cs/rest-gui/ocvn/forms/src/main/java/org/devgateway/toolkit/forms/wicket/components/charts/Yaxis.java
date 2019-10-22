package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;

/**
 * @author idobre
 * @since 4/21/17
 */
public class Yaxis implements Serializable {
    private final String title;

    private final Integer tickangle;

    private final Boolean zeroline;

    private final Integer gridwidth;

    private final Boolean autorange;

    private final String type;

    public Yaxis(final YaxisBuilder yaxisBuilder) {
        this.title = yaxisBuilder.title;
        this.tickangle = yaxisBuilder.tickangle;
        this.zeroline = yaxisBuilder.zeroline;
        this.gridwidth = yaxisBuilder.gridwidth;
        this.autorange = yaxisBuilder.autorange;
        this.type = yaxisBuilder.type;
    }

    public static class YaxisBuilder {
        private String title;

        private Integer tickangle;

        private Boolean zeroline;

        private Integer gridwidth;

        private Boolean autorange;

        private String type;

        public YaxisBuilder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public YaxisBuilder setTickangle(final Integer tickangle) {
            this.tickangle = tickangle;
            return this;
        }

        public YaxisBuilder setZeroline(final Boolean zeroline) {
            this.zeroline = zeroline;
            return this;
        }

        public YaxisBuilder setGridwidth(final Integer gridwidth) {
            this.gridwidth = gridwidth;
            return this;
        }

        public YaxisBuilder setAutorange(final Boolean autorange) {
            this.autorange = autorange;
            return this;
        }

        public YaxisBuilder setType(final String type) {
            this.type = type;
            return this;
        }

        public Yaxis build() {
            return new Yaxis(this);
        }
    }
}
