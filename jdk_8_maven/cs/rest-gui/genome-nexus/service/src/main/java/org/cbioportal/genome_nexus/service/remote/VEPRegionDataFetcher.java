package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
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

/*
 * This uses the genome nexus VEP wrapper service to get annotations. Hgvs
 * format is not supported without installing the full ensembl rest service, so
 * use custom region endpoints instead.
 */
@Component
public class VEPRegionDataFetcher extends BaseExternalResourceFetcher<VariantAnnotation>
{
    private static final String MAIN_QUERY_PARAM = "variant";
    private static final String PLACEHOLDER = "VARIANT";
    private static final String RESPONSE_TIMEOUT_FIELD_NAME = "responseTimeout";

    @Value("${gn_vep.timeout.seconds:0}")
    private Integer timeoutSeconds;

    private final ExternalResourceTransformer<VariantAnnotation> transformer;

    @Autowired
    public VEPRegionDataFetcher(ExternalResourceTransformer<VariantAnnotation> externalResourceTransformer,
                                @Value("${gn_vep.region.url:}") String vepRegionUrl)
    {
        super(vepRegionUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = externalResourceTransformer;
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), VariantAnnotation.class);
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), VariantAnnotation.class);
    }

    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams)
    {
        RestTemplate restTemplate = new RestTemplate();
        return (DBObject) restTemplate.getForObject(uri + getOptionalQueryString(), BasicDBObject.class);
    }

    @Override
    public DBObject fetchRawValue(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException
    {
        return this.postForObject(this.URI.replace("/" + PLACEHOLDER, "") + getOptionalQueryString(), requestBody);
    }

    public ExternalResourceTransformer getTransformer(){
        return transformer;
    }

    private String getOptionalQueryString() {
        if (timeoutSeconds == 0) {
            return "";
        }
        return "?" + RESPONSE_TIMEOUT_FIELD_NAME + "=" + timeoutSeconds;
    }

}
