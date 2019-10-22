package org.devgateway.toolkit.forms.wicket.components.charts;

import java.io.Serializable;

/**
 * @author idobre
 * @since 4/21/17
 */
public class Font implements Serializable {
    private final Integer size;

    private final String family;

    public Font(final FontBuilder fontBuilder) {
        this.size = fontBuilder.size;
        this.family = fontBuilder.family;
    }

    public static class FontBuilder {
        private Integer size;

        private String family;

        public FontBuilder setSize(final Integer size) {
            this.size = size;
            return this;
        }

        public FontBuilder setFamily(final String family) {
            this.family = family;
            return this;
        }

        public Font build() {
            return new Font(this);
        }
    }

}
