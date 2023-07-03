package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class NucleotideContextWebServiceException extends DefaultWebServiceException
{
    public NucleotideContextWebServiceException(String responseBody) {
        super(responseBody);
    }

    public NucleotideContextWebServiceException(String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
    }
}