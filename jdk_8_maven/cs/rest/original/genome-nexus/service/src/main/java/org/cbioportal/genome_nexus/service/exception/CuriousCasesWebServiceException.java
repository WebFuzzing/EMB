package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class CuriousCasesWebServiceException extends DefaultWebServiceException
{
    private String genomicLocation;

    public CuriousCasesWebServiceException(String genomicLocation, String responseBody) {
        super(responseBody);
        this.genomicLocation = genomicLocation;
    }

    public CuriousCasesWebServiceException(String genomicLocation, String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
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
        return "Curious cases web service error for " + this.getGenomicLocation();
    }
}
