package org.cbioportal.genome_nexus.service.remote;

import com.mongodb.BasicDBList;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Component
public class MyVariantInfoDataFetcher extends BaseExternalResourceFetcher<MyVariantInfo>
{
    private static final String MAIN_QUERY_PARAM = "variant";
    private static final String PLACEHOLDER = "VARIANT";

    private final ExternalResourceTransformer<MyVariantInfo> transformer;

    @Autowired
    public MyVariantInfoDataFetcher(ExternalResourceTransformer<MyVariantInfo> transformer,
                                       @Value("${myvariantinfo.url:https://myvariant.info/v1/variant/VARIANT}") String myVariantInfoUrl)
    {
        super(myVariantInfoUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    @Override
    public List<MyVariantInfo> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), MyVariantInfo.class);
    }

    @Override
    public List<MyVariantInfo> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), MyVariantInfo.class);
    }

    /**
     * By default assuming that response is a List. For all other response types
     * this method should be overridden.
     */
    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, BasicDBObject.class);
    }

    @Override
    protected DBObject postForObject(String uri, Object requestBody)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.postForObject(uri, request, BasicDBList.class);
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}
