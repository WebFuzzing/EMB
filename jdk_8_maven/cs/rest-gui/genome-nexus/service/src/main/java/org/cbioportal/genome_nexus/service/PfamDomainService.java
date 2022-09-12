package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.PfamDomain;
import org.cbioportal.genome_nexus.service.exception.PfamDomainNotFoundException;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface PfamDomainService
{
    PfamDomain getPfamDomainByPfamAccession(String pfamDomainAccession) throws PfamDomainNotFoundException;
    List<PfamDomain> getPfamDomainsByPfamAccession(List<String> pfamDomainAccessions);
}
