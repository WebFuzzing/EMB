package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class CancerHotspotsWebServiceException extends DefaultWebServiceException
{
    public CancerHotspotsWebServiceException(String responseBody) {
        super(responseBody);
    }

    public CancerHotspotsWebServiceException(String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
    }
}
