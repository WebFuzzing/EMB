package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Component
public class NucleotideContextDataFetcher extends BaseExternalResourceFetcher<NucleotideContext>
{
    private static final String MAIN_QUERY_PARAM = "query";
    private static final String PLACEHOLDER = "QUERY";

    private final ExternalResourceTransformer<NucleotideContext> transformer;

    @Autowired
    public NucleotideContextDataFetcher(ExternalResourceTransformer<NucleotideContext> transformer,
                               @Value("${ensembl.sequence.url:https://grch37.rest.ensembl.org/sequence/region/human/QUERY?content-type=application/json'}") String ensemblSequenceUrl)
    {
        super(ensemblSequenceUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    @Override
    public List<NucleotideContext> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), NucleotideContext.class);
    }

    @Override
    public List<NucleotideContext> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), NucleotideContext.class);
    }

    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, BasicDBObject.class);
    }


    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}