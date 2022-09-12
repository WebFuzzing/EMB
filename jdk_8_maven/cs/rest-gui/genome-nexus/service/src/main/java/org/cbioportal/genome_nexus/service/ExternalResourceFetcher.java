package org.cbioportal.genome_nexus.service;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

public interface ExternalResourceFetcher<T>
{
    Boolean hasValidURI();
    DBObject fetchRawValue(Map<String, String> queryParams) throws HttpClientErrorException, ResourceAccessException;
    DBObject fetchRawValue(String param) throws HttpClientErrorException, ResourceAccessException;
    DBObject fetchRawValue(Object requestBody) throws HttpClientErrorException, ResourceAccessException;
    List<T> fetchInstances(Map<String, String> queryParams) throws HttpClientErrorException, ResourceAccessException, ResourceMappingException;
    List<T> fetchInstances(String param) throws HttpClientErrorException, ResourceAccessException, ResourceMappingException;
    List<T> fetchInstances(Object requestBody) throws HttpClientErrorException, ResourceAccessException, ResourceMappingException;
}
