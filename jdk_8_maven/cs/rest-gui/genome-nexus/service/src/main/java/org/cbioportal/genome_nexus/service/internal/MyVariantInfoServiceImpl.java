package org.cbioportal.genome_nexus.service.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MyVariantInfoService;
import org.cbioportal.genome_nexus.service.cached.CachedMyVariantInfoFetcher;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.util.Hgvs;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class MyVariantInfoServiceImpl implements MyVariantInfoService
{
    private static final Log LOG = LogFactory.getLog(MyVariantInfoServiceImpl.class);

    private final CachedMyVariantInfoFetcher cachedExternalResourceFetcher;

    @Autowired
    public MyVariantInfoServiceImpl(CachedMyVariantInfoFetcher cachedExternalResourceFetcher)
    {
        this.cachedExternalResourceFetcher = cachedExternalResourceFetcher;
    }

    /**
     * @param variant   hgvs variant (ex: 7:g.140453136A>T)
     */
    public MyVariantInfo getMyVariantInfoByHgvsVariant(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MyVariantInfoWebServiceException, MyVariantInfoNotFoundException
    {
        return this.getMyVariantInfoByMyVariantInfoVariant(buildRequest(variant));
    }

    /**
     * @param variants   hgvs variants (ex: 7:g.140453136A>T)
     */
    public List<MyVariantInfo> getMyVariantInfoByHgvsVariant(List<String> variants)
    {
        List<MyVariantInfo> myVariantInfos = Collections.emptyList();

        Map<String, String> queryToVariant = this.queryToVariant(variants);
        List<String> queryVariants = new ArrayList<>(queryToVariant.keySet());

        try {
            myVariantInfos = this.getMyVariantInfoByMyVariantInfoVariant(queryVariants);
            // manually set the original hgvs variant field
            for (MyVariantInfo myVariantInfo: myVariantInfos) {
                myVariantInfo.setHgvs(queryToVariant.get(myVariantInfo.getQuery()));
            }
        } catch (MyVariantInfoWebServiceException e) {
            LOG.warn(e.getResponseBody());
        }

        return myVariantInfos;
    }

    public MyVariantInfo getMyVariantInfoByAnnotation(VariantAnnotation annotation)
        throws MyVariantInfoNotFoundException, MyVariantInfoWebServiceException
    {
        // get hgvsg from VEP (annotation.getId() might not be in hgvsg format)
        String hgvsg = annotation.getHgvsg();
        if (hgvsg != null) {
            return this.getMyVariantInfoByMyVariantInfoVariant(buildRequest(hgvsg));
        }
        else {
            return null;
        }
    }

    @Override
    public List<MyVariantInfo> getMyVariantInfoByAnnotation(List<VariantAnnotation> annotations)
        throws MyVariantInfoWebServiceException
    {
        return this.getMyVariantInfoByHgvsVariant(
            annotations.stream().map(VariantAnnotation::getHgvsg).collect(Collectors.toList())
        );
    }

    /**
     * @param variant my variant info variant (ex: chr1:g.35367G>A)
     */
    public MyVariantInfo getMyVariantInfoByMyVariantInfoVariant(String variant)
        throws MyVariantInfoNotFoundException, MyVariantInfoWebServiceException
    {
        try {
            // get the annotation from the web service and save it to the DB
            List<MyVariantInfo> myVariantInfoList = cachedExternalResourceFetcher.fetchAndCache(Arrays.asList(variant));
            // One variant was sent for My Variant Info annotation, return should have only one annotation result if available
            if (myVariantInfoList.size() == 1) {
                // manually set the original hgvs variant field
                MyVariantInfo myVariantInfo = myVariantInfoList.get(0);
                myVariantInfo.setHgvs(variant);
                return myVariantInfo;
            }
            else {
                throw new MyVariantInfoNotFoundException(variant);
            }
        } catch (ResourceMappingException e) {
            throw new MyVariantInfoWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MyVariantInfoWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new MyVariantInfoWebServiceException(e.getMessage());
        }
    }

    /**
     * @param variants my variant info variants (ex: [chr1:g.35367G>A, chr6:g.152708291G>A])
     */
    private List<MyVariantInfo> getMyVariantInfoByMyVariantInfoVariant(List<String> variants)
        throws MyVariantInfoWebServiceException
    {
        try {
            // get the annotations from the web service and save it to the DB
            return cachedExternalResourceFetcher.fetchAndCache(variants);
        } catch (ResourceMappingException e) {
            throw new MyVariantInfoWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MyVariantInfoWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new MyVariantInfoWebServiceException(e.getMessage());
        }
    }

    @NotNull
    private String buildRequest(String variant)
    {
        return Hgvs.addChrPrefix(Hgvs.removeDeletedBases(variant));
    }


    /**
     * Creates a LinkedHashMap to make sure that we keep the original input order.
     * Key is the string created by buildRequest method.
     * Value is the original variant string
     *
     * @param variants list of original input variants
     * @return LinkedHashMap
     */
    private Map<String, String> queryToVariant(List<String> variants) {
        return variants.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                this::buildRequest,
                v -> v,
                (u, v) -> v,
                LinkedHashMap::new
            )
        );
    }
}
