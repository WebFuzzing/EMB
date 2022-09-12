package org.cbioportal.genome_nexus.service.cached;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.persistence.GeneXrefRepository;
import org.cbioportal.genome_nexus.component.annotation.EntrezGeneXrefResolver;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.remote.GeneXrefDataFetcher;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

@Component
public class CachedEntrezGeneXrefFetcher
{
    private static final Log LOG = LogFactory.getLog(CachedEntrezGeneXrefFetcher.class);

    private final GeneXrefDataFetcher externalResourceFetcher;
    private final GeneXrefRepository repository;
    private final EntrezGeneXrefResolver entrezGeneXrefResolver;

    @Autowired
    public CachedEntrezGeneXrefFetcher(GeneXrefDataFetcher externalResourceFetcher,
                                       GeneXrefRepository repository,
                                       EntrezGeneXrefResolver entrezGeneXrefResolver)
    {
        this.externalResourceFetcher = externalResourceFetcher;
        this.repository = repository;
        this.entrezGeneXrefResolver = entrezGeneXrefResolver;
    }

    // TODO partial code duplication: see BaseCachedExternalResourceFetcher.fetchAndCache
    public GeneXref fetchAndCache(String accession, String geneSymbol)
        throws ResourceMappingException, HttpClientErrorException, ResourceAccessException
    {
        boolean saveValue = true;
        Optional<GeneXref> entrezGeneXrefOptional = null;

        try {
            entrezGeneXrefOptional = this.repository.findById(accession);
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on the external web service. " +
                "Will not attempt to store variant in Mongo database.");
            saveValue = false;
        }

        if (!entrezGeneXrefOptional.isPresent()) {
            // get the gene xref from the web service and save it to the DB
            try {
                GeneXref entrezGeneXref = this.fetchEntrezGeneXref(accession, geneSymbol);

                if (saveValue && entrezGeneXref != null) {
                    entrezGeneXref.setEnsemblGeneId(accession);
                    this.repository.save(entrezGeneXref);
                }
                return entrezGeneXref;
            }
            catch (DataIntegrityViolationException e) {
                // in case of data integrity violation exception, do not bloat the logs
                // this is thrown when the annotationJSON can't be stored by mongo
                // due to the variant annotation key being too large to index
                LOG.info(e.getLocalizedMessage());
            }
            catch (ResourceMappingException e) {
                LOG.info(e.getLocalizedMessage());
            }
        }

        try {
            return entrezGeneXrefOptional.get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    @Nullable
    public GeneXref fetchEntrezGeneXref(String accession, String geneSymbol)
        throws ResourceMappingException, HttpClientErrorException, ResourceAccessException
    {
        // get xrefs from the service
        List<GeneXref> geneXrefs = this.externalResourceFetcher.fetchInstances(accession);

        return this.entrezGeneXrefResolver.resolve(geneXrefs, geneSymbol);
    }
}
