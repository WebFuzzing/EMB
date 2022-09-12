package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.persistence.GenericMongoRepository;
import org.cbioportal.genome_nexus.service.CachedExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.ResourceTransformer;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.util.NaturalOrderComparator;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCachedExternalResourceFetcher<T, R extends MongoRepository<T, String> & GenericMongoRepository>
    implements CachedExternalResourceFetcher<T>
{
    private static final Log LOG = LogFactory.getLog(BaseCachedExternalResourceFetcher.class);

    protected String collection;
    protected R repository;
    protected Class<T> type;
    protected ExternalResourceFetcher<T> fetcher;
    protected ResourceTransformer<T> transformer;
    protected Integer maxPageSize;

    public BaseCachedExternalResourceFetcher(String collection,
                                             R repository,
                                             Class<T> type,
                                             ExternalResourceFetcher<T> fetcher,
                                             ResourceTransformer<T> transformer)
    {
        this(collection, repository, type, fetcher, transformer, Integer.MAX_VALUE);
    }

    public BaseCachedExternalResourceFetcher(String collection,
                                             R repository,
                                             Class<T> type,
                                             ExternalResourceFetcher<T> fetcher,
                                             ResourceTransformer<T> transformer,
                                             Integer maxPageSize)
    {
        this.collection = collection;
        this.repository = repository;
        this.type = type;
        this.fetcher = fetcher;
        this.transformer = transformer;
        this.maxPageSize = maxPageSize;
    }

    public Boolean hasValidURI() {
        return this.fetcher.hasValidURI();
    }

    // Needs to be overridden to support checking for valid ids
    protected Boolean isValidId(String id)
    {
        return true;
    }

    public T fetchAndCache(String id) throws ResourceMappingException
    {
        boolean saveRawValue = true;
        Optional<T> instance = null;

        if (!isValidId(id)) {
            return null;
        }

        try {
            instance = this.repository.findById(id);
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on the external web service. " +
                "Will not attempt to store variant in Mongo database.");
            saveRawValue = false;
        }
        if (!instance.isPresent())
        {
            // get the annotation from the web service and save it to the DB
            try {
                // get the raw annotation string from the web service
                DBObject rawValue = this.fetcher.fetchRawValue(id);
                // construct an instance to return:
                // this does not contain all the information obtained from the web service
                // only the fields mapped to the VariantAnnotation model will be returned
                rawValue = this.normalizeResponse(rawValue);
                List<T> list = this.transformer.transform(rawValue, this.type);

                if (list.size() > 0) {
                    instance = Optional.ofNullable(list.get(0));
                }

                // save everything to the cache as a properly parsed JSON
                if (saveRawValue) {
                    this.repository.saveDBObject(this.collection, id, rawValue);
                }
            }
            catch (DataIntegrityViolationException e) {
                // in case of data integrity violation exception, do not bloat the logs
                // this is thrown when the annotationJSON can't be stored by mongo
                // due to the variant annotation key being too large to index
                LOG.info(e.getLocalizedMessage());
            } catch (HttpServerErrorException e) {
                // failure fetching external resource
                LOG.error("Failure fetching external resource: " + e.getLocalizedMessage());
            }
        }

        try {
            return instance.get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public Map<String, T> constructFetchedMap(List<String> ids) throws ResourceMappingException
    {
        boolean saveValues = true;
        Set<String> uniqueIds = new LinkedHashSet<>(ids);
        Map<String, T> idToInstance = initIdToInstanceMap(uniqueIds);
        Set<String> alreadyCached = new LinkedHashSet<>();

        try {
            // add everything already cached into the map
            for (T instance: this.repository.findAllById(uniqueIds))
            {
                String id = this.extractId(instance);
                idToInstance.put(id, instance);
                alreadyCached.add(id);
            }
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on the external web service. " +
                "Will not attempt to store variant in Mongo database.");
            saveValues = false;
        }

        Set<String> needToFetch = new LinkedHashSet<>(uniqueIds);

        // remove already cached ids from the set, so that we don't query again
        needToFetch.removeAll(alreadyCached);

        // also remove invalid ids
        needToFetch = needToFetch.stream().filter(this::isValidId).collect(Collectors.toSet());
        // fetch missing instances
        if (needToFetch.size() > 0) {
            // get the annotation from the web service and save it to the DB
            this.fetchAndCache(needToFetch, idToInstance, saveValues);
        }
        return idToInstance;
    }

    public List<T> fetchAndCache(List<String> ids) throws ResourceMappingException
    {
        Map<String, T> idToInstance = constructFetchedMap(ids);
        Collection<T> values = idToInstance.values();
        values.removeIf(Objects::isNull);

        return new ArrayList<>(values);
    }

    protected void fetchAndCache(Set<String> needToFetch,
                                 Map<String, T> idToInstance,
                                 boolean saveValues) throws ResourceMappingException, HttpClientErrorException
    {
        // send up to maxPageSize entities per request
        for (Set<String> subSet: this.generateChunks(needToFetch))
        {
            DBObject rawValue = null;

            try {
                // get the raw annotation string from the web service
                rawValue = this.fetcher.fetchRawValue(this.buildRequestBody(subSet));
            } catch (HttpClientErrorException e) {
                LOG.error("HTTP ERROR " + e.getStatusCode() + " for " + subSet.toString() + ": " + e.getResponseBodyAsString(), e);
            } catch (RestClientException e) {
                LOG.error("REST ERROR [" +  e.getLocalizedMessage() + "] for " + subSet.toString(), e);
            }

            if (rawValue != null) {
                try {
                    rawValue = this.normalizeResponse(rawValue);

                    // fetch instances to return:
                    // this does not contain all the information obtained from the web service
                    // only the fields mapped to the VariantAnnotation model will be returned
                    List<T> fetched = this.transformer.transform(rawValue, this.type);
                    fetched.forEach(t -> idToInstance.put(this.extractId(t), t));

                    // save everything to the cache as a properly parsed JSON
                    if (saveValues) {
                        this.saveToDb(rawValue);
                    }
                } catch (DataIntegrityViolationException e) {
                    // in case of data integrity violation exception, do not bloat the logs
                    // this is thrown when the annotationJSON can't be stored by mongo
                    // due to the variant annotation key being too large to index
                    LOG.info(e.getLocalizedMessage());
                }
            }
        }
    }

    protected List<LinkedHashSet<String>> generateChunks(Set<String> needToFetch)
    {
        List<LinkedHashSet<String>> chunks = new ArrayList<>();
        List<String> sortedUncachedRegions = new ArrayList<>(needToFetch);
        Collections.sort(sortedUncachedRegions, new NaturalOrderComparator());

        // chunk size should be at most maxPageSize
        for (int i = 0; i < sortedUncachedRegions.size(); i += this.maxPageSize) {
            chunks.add(new LinkedHashSet<>(sortedUncachedRegions.subList(i, Math.min(sortedUncachedRegions.size(), i + this.maxPageSize))));
        }

        return chunks;
    }

    protected void saveToDb(DBObject rawValue)
    {
        List<DBObject> dbObjects = this.transformer.transform(rawValue);

        for (DBObject dbObject: dbObjects) {
            dbObject.put("_id", this.extractId(dbObject));
        }

        // filter out objects with invalid ids, do not save them to DB!
        dbObjects = dbObjects.stream()
            .filter(o -> o.get("_id") != null)
            .collect(Collectors.toList());

        this.repository.saveDBObjects(this.collection, dbObjects);
    }

    private Map<String, T> initIdToInstanceMap(Set<String> ids)
    {
        Map<String, T> map = new LinkedHashMap<>();

        // this will ensure the final map has the same order of the input ids
        ids.forEach(id -> map.put(id, null));

        return map;
    }

    // Needs to be overridden by child classes to support multi fetch and cache!
    protected String extractId(T instance)
    {
        return null;
    }

    // Needs to be overridden by child classes to support multi fetch and cache!
    protected String extractId(DBObject dbObject)
    {
        return null;
    }

    // No sanitization by default, just assume that response is consistent
    protected DBObject normalizeResponse(DBObject rawValue) {
        return rawValue;
    }

    protected Object buildRequestBody(Set<String> ids)
    {
        return ids;
    }
}
