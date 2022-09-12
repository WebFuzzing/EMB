package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.PdbDataService;
import org.cbioportal.genome_nexus.service.cached.CachedPdbHeaderFetcher;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class PdbDataServiceImpl implements PdbDataService
{
    private static final Log LOG = LogFactory.getLog(PdbDataServiceImpl.class);

    private final CachedPdbHeaderFetcher cachedExternalResourceFetcher;

    @Autowired
    public PdbDataServiceImpl(CachedPdbHeaderFetcher cachedExternalResourceFetcher)
    {
        this.cachedExternalResourceFetcher = cachedExternalResourceFetcher;
    }

    @Override
    public PdbHeader getPdbHeader(String pdbId) throws PdbHeaderNotFoundException, PdbHeaderWebServiceException
    {
        Optional<PdbHeader> pdbHeader = null;

        try {
            // get the PDB data from the web service and save it to the DB
            pdbHeader = Optional.of(this.cachedExternalResourceFetcher.fetchAndCache(pdbId));

            // include original pdb id value too
            pdbHeader.ifPresent(x -> x.setPdbId(pdbId));
        }
        catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a problem with the service.
            throw new PdbHeaderWebServiceException(pdbId, e.getResponseBodyAsString(), e.getStatusCode());
        }
        catch (ResourceMappingException e) {
            // TODO this only indicates that web service returns an incompatible response, but
            // this does not always mean that annotation is not found
            throw new PdbHeaderNotFoundException(pdbId);
        }
        catch (ResourceAccessException e) {
            throw new PdbHeaderWebServiceException(pdbId, e.getMessage());
        }

        try {
            return pdbHeader.get();
        } catch(NoSuchElementException e) {
            throw new PdbHeaderNotFoundException(pdbId);
        }
    }

    @Override
    public List<PdbHeader> getPdbHeaders(List<String> pdbIds)
    {
        List<PdbHeader> pdbHeaderList = new LinkedList<>();

        // remove duplicates
        Set<String> pdbIdSet = new LinkedHashSet<>(pdbIds);

        for (String pdbId : pdbIdSet)
        {
            try {
                PdbHeader header = this.getPdbHeader(pdbId);
                pdbHeaderList.add(header);
            } catch (PdbHeaderWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (PdbHeaderNotFoundException e) {
                // fail silently for this pdb id
            }
        }

        return pdbHeaderList;
    }
}
