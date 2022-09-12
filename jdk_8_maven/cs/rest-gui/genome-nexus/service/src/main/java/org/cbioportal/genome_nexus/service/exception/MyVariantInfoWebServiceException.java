package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class MyVariantInfoWebServiceException extends DefaultWebServiceException
{
    public MyVariantInfoWebServiceException(String responseBody) {
        super(responseBody);
    }

    public MyVariantInfoWebServiceException(String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
    }
}
