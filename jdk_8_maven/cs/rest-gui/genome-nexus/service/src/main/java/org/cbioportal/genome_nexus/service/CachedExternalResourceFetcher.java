package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;

import java.util.List;
import java.util.Map;

public interface CachedExternalResourceFetcher<T>
{
    T fetchAndCache(String id) throws ResourceMappingException;
    Map<String, T> constructFetchedMap(List<String> ids) throws ResourceMappingException;
    List<T> fetchAndCache(List<String> id) throws ResourceMappingException;
}
