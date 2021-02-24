package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;
import java.util.List;

/**
 * @author idobre
 * @since 4/19/17
 */
public final class Layout implements Serializable {
    private final Integer width;

    private final Integer height;

    private final String barmode;

    private final String title;

    private final List<Annotation> annotations;

    private final Font font;

    private final Boolean showlegend;

    private final Xaxis xaxis;

    private final Yaxis yaxis;

    private final Double bargap;

    public Layout(final LayoutBuilder layoutBuilder) {
        this.width = layoutBuilder.width;
        this.height = layoutBuilder.height;
        this.barmode = layoutBuilder.barmode;
        this.title = layoutBuilder.title;
        this.annotations = layoutBuilder.annotations;
        this.font = layoutBuilder.font;
        this.showlegend = layoutBuilder.showlegend;
        this.xaxis = layoutBuilder.xaxis;
        this.yaxis = layoutBuilder.yaxis;
        this.bargap = layoutBuilder.bargap;
    }

    public static class LayoutBuilder {
        private Integer width;

        private Integer height;

        private String barmode;

        private String title;

        private List<Annotation> annotations;

        private Font font;

        private Boolean showlegend;

        private Xaxis xaxis;

        private Yaxis yaxis;

        private Double bargap;

        public LayoutBuilder setWidth(final Integer width) {
            this.width = width;
            return this;
        }

        public LayoutBuilder setHeight(final Integer height) {
            this.height = height;
            return this;
        }

        public LayoutBuilder setBarmode(final String barmode) {
            this.barmode = barmode;
            return this;
        }

        public LayoutBuilder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public LayoutBuilder setAnnotations(final List<Annotation> annotations) {
            this.annotations = annotations;
            return this;
        }

        public LayoutBuilder setFont(final Font font) {
            this.font = font;
            return this;
        }

        public LayoutBuilder setShowlegend(final Boolean showlegend) {
            this.showlegend = showlegend;
            return this;
        }

        public LayoutBuilder setXaxis(final Xaxis xaxis) {
            this.xaxis = xaxis;
            return this;
        }

        public LayoutBuilder setYaxis(final Yaxis yaxis) {
            this.yaxis = yaxis;
            return this;
        }

        public LayoutBuilder setBargap(final Double bargap) {
            this.bargap = bargap;
            return this;
        }

        public Layout build() {
            return new Layout(this);
        }
    }
}
