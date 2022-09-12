package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.PfamDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface PfamDomainRepository extends MongoRepository<PfamDomain, String>
{
    PfamDomain findOneByPfamAccession(String pfamDomainAccession);
    List<PfamDomain> findByPfamAccessionIn(List<String> pfamAccessions);
}
