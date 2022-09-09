package org.cbioportal.genome_nexus.service.remote;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base abstract implementation of ExternalResourceFetcher.
 * Provides a generic implementation for convenience.
 *
 * @author Selcuk Onur Sumer
 */
public abstract class BaseExternalResourceFetcher<T> implements ExternalResourceFetcher<T>
{
    protected String URI;
    protected String mainQueryParam;
    protected String placeholder;

    public BaseExternalResourceFetcher(String URI, String mainQueryParam, String placeholder)
    {
        this.URI = URI;
        this.mainQueryParam = mainQueryParam;
        this.placeholder = placeholder;
    }

    @Override
    public Boolean hasValidURI() {
        return this.URI.length() > 0;
    }

    /**
     * Base implementation for a single parameter query.
     * This method should be overridden if the query has more than one parameter.
     */
    @Override
    public DBObject fetchRawValue(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException
    {
        // get the value of the main (single) parameter
        String paramValue = queryParams.get(this.mainQueryParam);
        String uri = this.URI;

        // replace the placeholder with the value in the uri
        if (paramValue != null && paramValue.length() > 0) {
            uri = uri.replace(this.placeholder, paramValue);
        }

        return this.getForObject(uri, queryParams);
    }

    @Override
    public DBObject fetchRawValue(String param)
        throws HttpClientErrorException, ResourceAccessException
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(this.mainQueryParam, param);

        return this.fetchRawValue(queryParams);
    }

    @Override
    public DBObject fetchRawValue(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException
    {
        return this.postForObject(this.URI, requestBody);
    }

    @Override
    public List<T> fetchInstances(String param)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(this.mainQueryParam, param);

        return this.fetchInstances(queryParams);
    }

    /**
     * By default assuming that response is a List. For all other response types this method should be overridden.
     */
    protected DBObject getForObject(String uri, Map<String, String> queryParams)
    {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, BasicDBList.class);
    }

    /**
     * By default assuming that response is a List. For all other response types this method should be overridden.
     */
    protected DBObject postForObject(String uri, Object requestBody)
    {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForObject(uri, requestBody, BasicDBList.class);
    }
}
