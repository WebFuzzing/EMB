package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class MutationAssessorWebServiceException extends DefaultWebServiceException
{
    public MutationAssessorWebServiceException(String responseBody) {
        super(responseBody);
    }

    public MutationAssessorWebServiceException(String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
    }
}
