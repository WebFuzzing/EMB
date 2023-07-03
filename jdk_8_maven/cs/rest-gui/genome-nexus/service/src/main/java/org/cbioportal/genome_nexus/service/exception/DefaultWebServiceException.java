package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class DefaultWebServiceException extends Exception
{
    private String responseBody;
    private HttpStatus statusCode;

    public DefaultWebServiceException(String responseBody) {
        super();
        this.responseBody = responseBody;
        this.statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }

    public DefaultWebServiceException(String responseBody, HttpStatus statusCode) {
        super();
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }
}
