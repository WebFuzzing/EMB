package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

@Component
public class MutationAssessorDataFetcher extends BaseExternalResourceFetcher<MutationAssessor>
{
    private static final String MAIN_QUERY_PARAM = "variant";
    private static final String PLACEHOLDER = "VARIANT";

    private final ExternalResourceTransformer<MutationAssessor> transformer;

    @Autowired
    public MutationAssessorDataFetcher(ExternalResourceTransformer<MutationAssessor> transformer,
                                       @Value("${mutationAssessor.url}") String mutationAssessorUrl)
    {
        super(mutationAssessorUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    @Override
    public List<MutationAssessor> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), MutationAssessor.class);
    }

    @Override
    public List<MutationAssessor> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), MutationAssessor.class);
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}
