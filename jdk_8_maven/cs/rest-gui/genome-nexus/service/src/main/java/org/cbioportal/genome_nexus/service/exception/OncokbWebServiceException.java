package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class OncokbWebServiceException extends DefaultWebServiceException
{
    public OncokbWebServiceException(String responseBody) {
        super(responseBody);
    }

    public OncokbWebServiceException(String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
    }
}
