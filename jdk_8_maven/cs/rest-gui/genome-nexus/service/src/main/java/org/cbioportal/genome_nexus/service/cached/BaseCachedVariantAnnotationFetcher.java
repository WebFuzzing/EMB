package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
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
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCachedVariantAnnotationFetcher
    extends BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository>
{
    private static final Log LOG = LogFactory.getLog(BaseCachedVariantAnnotationFetcher.class);

    protected String collection;
    protected VariantAnnotationRepository repository;
    protected Class<VariantAnnotation> type;
    protected ExternalResourceFetcher<VariantAnnotation> fetcher;
    protected ResourceTransformer<VariantAnnotation> transformer;
    protected Integer maxPageSize;

    public BaseCachedVariantAnnotationFetcher(String collection,
                                             VariantAnnotationRepository repository,
                                             Class<VariantAnnotation> type,
                                             ExternalResourceFetcher<VariantAnnotation> fetcher,
                                             ResourceTransformer<VariantAnnotation> transformer)
    {
        super(collection, repository, type, fetcher, transformer, Integer.MAX_VALUE);
    }

    public BaseCachedVariantAnnotationFetcher(String collection,
                                             VariantAnnotationRepository repository,
                                             Class<VariantAnnotation> type,
                                             ExternalResourceFetcher<VariantAnnotation> fetcher,
                                             ResourceTransformer<VariantAnnotation> transformer,
                                             Integer maxPageSize)
    {
        super(collection, repository, type, fetcher, transformer, maxPageSize);
    }
   
    @Override
    protected String extractId(VariantAnnotation instance)
    {
        return instance.getVariantId();
    }

    @Override
    protected String extractId(DBObject dbObject)
    {
        return (String)dbObject.get("input");
    }

    @Override 
    public VariantAnnotation fetchAndCache(String id) throws ResourceMappingException
    {
        VariantAnnotation variantAnnotation = null;
        try {
            variantAnnotation = super.fetchAndCache(id);
            if (variantAnnotation == null) {
                variantAnnotation = new VariantAnnotation(id);
            } else {
                variantAnnotation.setSuccessfullyAnnotated(true);
            }
        } catch (Exception e) {
            return new VariantAnnotation(id);
        }
        return variantAnnotation;
    }
    
    @Override
    public List<VariantAnnotation> fetchAndCache(List<String> ids) throws ResourceMappingException
    {
        Map<String, VariantAnnotation> variantResponse = this.constructFetchedMap(ids); 
        for (String variantId : variantResponse.keySet()) {
            if (variantResponse.get(variantId) == null) {
                VariantAnnotation variantAnnotation = new VariantAnnotation(variantId);
                variantResponse.put(variantId, variantAnnotation);
            } else {
                variantResponse.get(variantId).setSuccessfullyAnnotated(true);
            }
        }
        // some util function to return response (values) with duplicates
        // need to pass in a list representing indexes of original request
        List<VariantAnnotation> values = new ArrayList();
        for (String id : ids) {
           values.add(variantResponse.get(id));
        } 
        return values;
    }
}
