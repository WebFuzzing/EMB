package org.cbioportal.genome_nexus.service.exception;

public class VariantAnnotationNotFoundException extends Exception
{
    private String variant;

    public VariantAnnotationNotFoundException(String variant) {
        super();
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    @Override
    public String getMessage() {
        return "Variant annotation not found: " + this.getVariant();
    }
}
