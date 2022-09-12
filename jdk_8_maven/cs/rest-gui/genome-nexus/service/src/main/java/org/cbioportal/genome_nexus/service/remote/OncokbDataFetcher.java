package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.oncokb.client.IndicatorQueryResp;


@Component
public class OncokbDataFetcher extends BaseExternalResourceFetcher<IndicatorQueryResp>
{
    private String oncokbToken;
    private static final String MAIN_QUERY_PARAM = "alteration";
    private static final String PLACEHOLDER = "PROTEINCHANGE";

    private final ExternalResourceTransformer<IndicatorQueryResp> transformer;

    @Autowired
    public OncokbDataFetcher(ExternalResourceTransformer<IndicatorQueryResp> transformer,
                                       @Value("${oncokb.url:https://www.oncokb.org/api/v1/annotate/mutations/byProteinChange?PROTEINCHANGE}") String oncokbUrl)
    {
        super(oncokbUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    public String getOncokbToken() {
        return oncokbToken;
    }

    public void setOncokbToken(String oncokbToken) {
        this.oncokbToken = oncokbToken;
    }

    @Override
    public List<IndicatorQueryResp> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), IndicatorQueryResp.class);
    }

    @Override
    public List<IndicatorQueryResp> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), IndicatorQueryResp.class);
    }

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
    public DBObject fetchRawValue(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException
    {
        return this.postForObject(this.URI, requestBody);
    }

    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + this.oncokbToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>(queryParams.toString(), httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<BasicDBObject> response = restTemplate.exchange(
            uri, HttpMethod.GET, entity, BasicDBObject.class);
        return response.getBody();
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }

    @Override
    protected DBObject postForObject(String uri, Object requestBody)
    {

        HttpHeaders httpHeaders = new HttpHeaders();        
        httpHeaders.add("Authorization", "Bearer " + this.oncokbToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);      
        
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), httpHeaders);
        return restTemplate.postForObject(uri, request, BasicDBObject.class);
    }
}
