package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;

/**
 * @author idobre
 * @since 4/21/17
 */
public class Xaxis implements Serializable {
    private final String title;

    private final Integer tickangle;

    private final Boolean zeroline;

    private final Integer gridwidth;

    private final Boolean autorange;

    private final String type;

    public Xaxis(final XaxisBuilder xaxisBuilder) {
        this.title = xaxisBuilder.title;
        this.tickangle = xaxisBuilder.tickangle;
        this.zeroline = xaxisBuilder.zeroline;
        this.gridwidth = xaxisBuilder.gridwidth;
        this.autorange = xaxisBuilder.autorange;
        this.type = xaxisBuilder.type;
    }

    public static class XaxisBuilder {
        private String title;

        private Integer tickangle;

        private Boolean zeroline;

        private Integer gridwidth;

        private Boolean autorange;

        private String type;

        public XaxisBuilder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public XaxisBuilder setTickangle(final Integer tickangle) {
            this.tickangle = tickangle;
            return this;
        }

        public XaxisBuilder setZeroline(final Boolean zeroline) {
            this.zeroline = zeroline;
            return this;
        }

        public XaxisBuilder setGridwidth(final Integer gridwidth) {
            this.gridwidth = gridwidth;
            return this;
        }

        public XaxisBuilder setAutorange(final Boolean autorange) {
            this.autorange = autorange;
            return this;
        }

        public XaxisBuilder setType(final String type) {
            this.type = type;
            return this;
        }

        public Xaxis build() {
            return new Xaxis(this);
        }
    }
}
