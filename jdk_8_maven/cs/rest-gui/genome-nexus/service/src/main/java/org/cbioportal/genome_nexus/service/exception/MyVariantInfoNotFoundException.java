package org.cbioportal.genome_nexus.service.exception;

public class MyVariantInfoNotFoundException extends Exception
{
    private String variant;

    public MyVariantInfoNotFoundException(String variant) {
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
        return "My Variant Info annotation not found for variant: " + this.getVariant();
    }
}
