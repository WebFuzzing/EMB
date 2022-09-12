package org.cbioportal.genome_nexus.service.cached;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.util.Strings;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.persistence.MyVariantInfoRepository;
import org.cbioportal.genome_nexus.persistence.internal.MyVariantInfoRepositoryImpl;
import org.cbioportal.genome_nexus.service.config.ExternalResourceObjectMapper;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.remote.MyVariantInfoDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.util.Transformer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CachedMyVariantInfoFetcher extends BaseCachedExternalResourceFetcher<MyVariantInfo, MyVariantInfoRepository>
{
    private static final Log LOG = LogFactory.getLog(CachedMyVariantInfoFetcher.class);

    private final ExternalResourceObjectMapper objectMapper;

    @Autowired
    public CachedMyVariantInfoFetcher(
        ExternalResourceTransformer<MyVariantInfo> transformer,
        MyVariantInfoRepository repository,
        MyVariantInfoDataFetcher fetcher,
        @Value("${myvariantinfo.max_page_size:500}") Integer maxPageSize
    )
    {
        super(
            MyVariantInfoRepositoryImpl.COLLECTION,
            repository,
            MyVariantInfo.class,
            fetcher,
            transformer,
            maxPageSize
        );

        // instantiate a custom object mapper for normalizing purposes
        this.objectMapper = new ExternalResourceObjectMapper();
        this.objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    protected Object buildRequestBody(Set<String> ids)
    {
        MultiValueMap<String, String> requestBody= new LinkedMultiValueMap<>();
        requestBody.add("ids", Strings.join(ids, ','));
        // TODO this way it looks cleaner, but it only fetches one result
        // requestBody.put("ids", new ArrayList<>(ids));

        return requestBody;
    }

    @Override
    protected String extractId(MyVariantInfo instance)
    {
        return instance.getVariant();
    }

    @Override
    protected String extractId(DBObject dbObject)
    {
        return (String)dbObject.get("_id");
    }

    @Override
    protected DBObject normalizeResponse(DBObject rawJson)
    {
        List<DBObject> curatedList = new ArrayList<>();

        // MyVariantInfo returns dynamic model (like error responses or inconsistent data model)
        // which we cannot map exactly to our static model.
        // Remove/normalize problematic ones so that we don't save those into the database
        for (DBObject dbObject: Transformer.convertToDbObjectList(rawJson))
        {
            try {
                // check for any errors (this throws exception if the response is an error JSON)
                this.checkForErrorResponse(dbObject);

                // convert DBObject to a proper instance of the given class type
                this.objectMapper.convertValue(dbObject, this.type);

                // if can be successfully converted normalize and add to the curated list
                curatedList.add(this.normalizeRawEntity(dbObject));
            } catch (MyVariantInfoNotFoundException e) {
                // fail silently for NotFoundException
                // TODO we should cache "not found" queries somewhere
            } catch (Exception e) {
                LOG.warn(this.getId(dbObject) + ": " + e.getLocalizedMessage());
            }
        }

        BasicDBList list = new BasicDBList();
        list.addAll(curatedList);
        return list;
    }

    private Object getId(@NotNull DBObject dbObject) {
        return dbObject.get("_id") != null ? dbObject.get("_id"): dbObject.get("query");
    }

    private void checkForErrorResponse(@NotNull DBObject dbObject) throws MyVariantInfoNotFoundException
    {
        if (dbObject.get("notfound") != null) {
            throw new MyVariantInfoNotFoundException(this.getId(dbObject).toString());
        }
    }

    private DBObject normalizeRawEntity(DBObject rawJson)
    {
        if (rawJson.get("dbsnp") instanceof Map) {
            Map<String, Object> dbsnp = (Map<String, Object>) rawJson.get("dbsnp");
            dbsnp.put("alleles", convertToList(dbsnp.get("alleles")));
            dbsnp.put("flags", convertToList(dbsnp.get("flags")));
        }

        if (rawJson.get("clinvar") instanceof Map) {
            Map<String, Object> clinvar = (Map<String, Object>) rawJson.get("clinvar");
            clinvar.put("rcv", convertToList(clinvar.get("rcv")));
            Map<String, Object> hgvs = (Map<String, Object>) clinvar.get("hgvs");
            hgvs.put("coding", convertToList(hgvs.get("coding")));
        }

        return rawJson;
    }

    private List<Object> convertToList(Object listOrSingleValue)
    {
        if (listOrSingleValue instanceof List) {
            return (List<Object>)listOrSingleValue;
        }
        else {
            return Collections.singletonList(listOrSingleValue);
        }
    }
}
