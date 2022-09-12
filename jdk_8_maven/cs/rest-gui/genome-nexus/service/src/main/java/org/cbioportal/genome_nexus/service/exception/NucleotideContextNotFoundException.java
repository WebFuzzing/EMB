package org.cbioportal.genome_nexus.service.exception;

public class NucleotideContextNotFoundException extends Exception
{
    private String variant;

    public NucleotideContextNotFoundException(String variant)
    {
        super();
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    public void sethugoSymbol(String variant) {
        this.variant = variant;
    }

    @Override
    public String getMessage() {
        return "NucleotideContext not found: " + this.getVariant();
    }
}