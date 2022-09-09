
package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.NucleotideContextService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.cached.CachedNucleotideContextFetcher;
import org.cbioportal.genome_nexus.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class NucleotideContextServiceImpl implements NucleotideContextService
{
    private static final Log LOG = LogFactory.getLog(NucleotideContextServiceImpl.class);

    private final CachedNucleotideContextFetcher cachedExternalResourceFetcher;
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public NucleotideContextServiceImpl(CachedNucleotideContextFetcher cachedExternalResourceFetcher,
                                       VariantAnnotationService verifiedHgvsVariantAnnotationService)
    {
        this.cachedExternalResourceFetcher = cachedExternalResourceFetcher;
        this.variantAnnotationService = verifiedHgvsVariantAnnotationService;
    }

    /**
     * @param variant   hgvs variant (ex: 7:g.140453136A>T)
     */
    public NucleotideContext getNucleotideContext(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        NucleotideContextNotFoundException, NucleotideContextWebServiceException
    {
        VariantAnnotation variantAnnotation = this.variantAnnotationService.getAnnotation(variant);

        return this.getNucleotideContextByVariantAnnotation(variantAnnotation);
    }

    /**
     * @param variants   hgvs variants (ex: 7:g.140453136A>T)
     */
    public List<NucleotideContext> getNucleotideContext(List<String> variants)
    {
        List<NucleotideContext> nucleotideContexts = new ArrayList<>();
        List<VariantAnnotation> variantAnnotations = this.variantAnnotationService.getAnnotations(variants);

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            try {
                nucleotideContexts.add(this.getNucleotideContextByVariantAnnotation(variantAnnotation));
            } catch (NucleotideContextWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (NucleotideContextNotFoundException e) {
                // fail silently for this variant
            }
        }

        return nucleotideContexts;
    }

    public NucleotideContext getNucleotideContext(VariantAnnotation annotation)
        throws NucleotideContextNotFoundException, NucleotideContextWebServiceException
    {
        // checks annotation is SNP
        if (annotation.getStart() == null
            || !annotation.getStart().equals(annotation.getEnd())
            || !annotation.getAlleleString().matches("[A-Z]/[A-Z]"))
        {
            throw new NucleotideContextNotFoundException(annotation.getVariant());
        }

        NucleotideContext nucleotideContext = this.getNucleotideContextByEnsembleSequenceQuery(buildRequest(annotation));

        // add original hgvs variant value too
        nucleotideContext.setHgvs(annotation.getVariant());

        return nucleotideContext;
    }

    /**
     * @param variant   mutation assessor variant (ex: 7,140453136,A,T)
     */
    public NucleotideContext getNucleotideContextByEnsembleSequenceQuery(String sequenceQuery)
        throws NucleotideContextNotFoundException, NucleotideContextWebServiceException
    {
        Optional<NucleotideContext> nucleotideContext = null;

        try {
            // get the annotation from the web service and save it to the DB
            nucleotideContext = Optional.of(cachedExternalResourceFetcher.fetchAndCache(sequenceQuery));
        } catch (ResourceMappingException e) {
            throw new NucleotideContextWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new NucleotideContextWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NucleotideContextWebServiceException(e.getMessage());
        }

        try {
            return nucleotideContext.get();
        } catch (NoSuchElementException e) {
            throw new NucleotideContextNotFoundException(sequenceQuery);
        }
    }

    private NucleotideContext getNucleotideContextByVariantAnnotation(VariantAnnotation variantAnnotation)
        throws NucleotideContextWebServiceException, NucleotideContextNotFoundException
    {
        NucleotideContext nucleotideContextObj = this.getNucleotideContext(variantAnnotation);

        if (nucleotideContextObj != null)
        {
            return nucleotideContextObj;
        }
        else {
            throw new NucleotideContextNotFoundException(variantAnnotation.getVariant());
        }
    }

    private String buildRequest(VariantAnnotation annotation)
    {
        // e.g. 17:37880219..37880221:1
        StringBuilder sb = new StringBuilder(annotation.getSeqRegionName() + ":");
        sb.append((annotation.getStart() - 1) + "..");
        sb.append(annotation.getStart() + 1 + ":1");

        return sb.toString();
    }
}
