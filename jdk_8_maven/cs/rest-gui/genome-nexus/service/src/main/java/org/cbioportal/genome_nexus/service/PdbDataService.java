package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderNotFoundException;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderWebServiceException;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface PdbDataService
{
    PdbHeader getPdbHeader(String pdbId) throws PdbHeaderNotFoundException, PdbHeaderWebServiceException;
    List<PdbHeader> getPdbHeaders(List<String> pdbIds);
}
