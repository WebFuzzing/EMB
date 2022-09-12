package org.cbioportal.genome_nexus.service.remote;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.PdbHeaderTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class PdbHeaderDataFetcher extends BaseExternalResourceFetcher<PdbHeader>
{
    private static final String MAIN_QUERY_PARAM = "pdbId";
    private static final String PLACEHOLDER = "PDB_ID";

    private final PdbHeaderTransformer transformer;

    @Autowired
    public PdbHeaderDataFetcher(PdbHeaderTransformer transformer,
                                @Value("${pdb.header_service_url}") String headerServiceUrl)
    {
        //http://www.rcsb.org/pdb/files/PDB_ID.pdb?headerOnly=YES
        //http://files.rcsb.org/header/PDB_ID.pdb
        super(headerServiceUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    @Override
    public List<PdbHeader> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        DBObject rawValue = this.fetchRawValue(queryParams);

        return this.transformer.transform(rawValue, PdbHeader.class);
    }

    @Override
    public List<PdbHeader> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), PdbHeader.class);
    }

    /**
     * We need to override this method, since PDB header response is plain text.
     */
    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams)
    {
        RestTemplate restTemplate = new RestTemplate();

        // read to string as plain text
        String response = restTemplate.getForObject(uri, String.class);
        String pdbId = queryParams.get(MAIN_QUERY_PARAM);

        // construct a new simple DBObject with the pdbId and response pair
        return new BasicDBObject(pdbId, response);
    }

    public PdbHeaderTransformer getTransformer() {
        return transformer;
    }
}
