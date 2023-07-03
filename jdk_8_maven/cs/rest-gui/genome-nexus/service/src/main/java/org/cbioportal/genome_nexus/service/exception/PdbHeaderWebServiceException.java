package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class PdbHeaderWebServiceException extends DefaultWebServiceException
{
    private String pdbId;

    public PdbHeaderWebServiceException(String pdbId, String responseBody) {
        super(responseBody);
        this.pdbId = pdbId;
    }

    public PdbHeaderWebServiceException(String pdbId, String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
        this.pdbId = pdbId;
    }

    public String getPdbId() {
        return pdbId;
    }

    public void setPdbId(String pdbId) {
        this.pdbId = pdbId;
    }

    @Override
    public String getMessage() {
        return "PDB web service error for id " + this.getPdbId() + ": " + this.getResponseBody();
    }
}
