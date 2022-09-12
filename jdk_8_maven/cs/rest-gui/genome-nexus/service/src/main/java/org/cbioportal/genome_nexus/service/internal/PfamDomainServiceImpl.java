package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.PfamDomain;
import org.cbioportal.genome_nexus.persistence.PfamDomainRepository;
import org.cbioportal.genome_nexus.service.PfamDomainService;
import org.cbioportal.genome_nexus.service.exception.PfamDomainNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class PfamDomainServiceImpl implements PfamDomainService
{
    private final PfamDomainRepository pfamDomainRepository;

    @Autowired
    public PfamDomainServiceImpl(PfamDomainRepository pfamDomainRepository)
    {
        this.pfamDomainRepository = pfamDomainRepository;
    }

    @Override
    public PfamDomain getPfamDomainByPfamAccession(String pfamDomainAccession) throws PfamDomainNotFoundException
    {
        PfamDomain domain = this.pfamDomainRepository.findOneByPfamAccession(pfamDomainAccession);

        if (domain == null) {
            throw new PfamDomainNotFoundException(pfamDomainAccession);
        }

        return domain;
    }

    @Override
    public List<PfamDomain> getPfamDomainsByPfamAccession(List<String> pfamDomainAccessions)
    {
        return this.pfamDomainRepository.findByPfamAccessionIn(pfamDomainAccessions);
    }
}
