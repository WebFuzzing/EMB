package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class EnsemblWebServiceException extends DefaultWebServiceException
{
    public EnsemblWebServiceException(String responseBody) {
        super(responseBody);
    }

    public EnsemblWebServiceException(String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
    }
}
