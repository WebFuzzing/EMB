package org.cbioportal.genome_nexus.service.exception;

public class VariantAnnotationQueryMixedFormatException extends Exception {

    public VariantAnnotationQueryMixedFormatException() {
        super();
    }

    public VariantAnnotationQueryMixedFormatException(String message) {
        super(message);
    }

    public VariantAnnotationQueryMixedFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariantAnnotationQueryMixedFormatException(Throwable cause) {
        super(cause);
    }

}
