package org.cbioportal.genome_nexus.service.exception;

public class CuriousCasesNotFoundException extends Exception
{
    private String genomicLocation;

    public CuriousCasesNotFoundException(String genomicLocation) {
        super();
        this.genomicLocation = genomicLocation;
    }

    public String getGenomicLocation() {
        return genomicLocation;
    }

    public void setGenomicLocation(String genomicLocation) {
        this.genomicLocation = genomicLocation;
    }

    @Override
    public String getMessage() {
        return "Curious cases comment not found: " + this.getGenomicLocation() + ". Submit potential new curious cases to https://bit.ly/2U1aEcj";
    }
}
