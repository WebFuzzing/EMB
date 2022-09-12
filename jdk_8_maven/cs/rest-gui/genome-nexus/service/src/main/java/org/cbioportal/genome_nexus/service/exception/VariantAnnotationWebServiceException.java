package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class VariantAnnotationWebServiceException extends DefaultWebServiceException
{
    private String variant;

    public VariantAnnotationWebServiceException(String variant, String responseBody) {
        super(responseBody);
        this.variant = variant;
    }

    public VariantAnnotationWebServiceException(String variant, String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
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
        return "VEP web service error for variant " + this.getVariant() + ": " + this.getResponseBody();
    }
}
