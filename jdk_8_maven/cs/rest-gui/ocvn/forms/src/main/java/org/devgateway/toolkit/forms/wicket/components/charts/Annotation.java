package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;

/**
 * @author idobre
 * @since 4/21/17
 */
public class Annotation implements Serializable {
    private final Font font;

    private final Boolean showarrow;

    private final String text;

    private final Double x;

    private final Double y;

    public Annotation(final AnnotationBuilder annotationBuilder) {
        this.font = annotationBuilder.font;

        this.showarrow = annotationBuilder.showarrow;
        this.text = annotationBuilder.text;
        this.x = annotationBuilder.x;
        this.y = annotationBuilder.y;
    }

    public static class AnnotationBuilder {
        private Font font;
        private Boolean showarrow;
        private String text;
        private Double x;
        private Double y;

        public AnnotationBuilder setFont(final Font font) {
            this.font = font;
            return this;
        }

        public AnnotationBuilder setShowarrow(final Boolean showarrow) {
            this.showarrow = showarrow;
            return this;
        }

        public AnnotationBuilder setText(final String text) {
            this.text = text;
            return this;
        }

        public AnnotationBuilder setX(final Double x) {
            this.x = x;
            return this;
        }

        public AnnotationBuilder setY(final Double y) {
            this.y = y;
            return this;
        }

        public Annotation build() {
            return new Annotation(this);
        }
    }
}
