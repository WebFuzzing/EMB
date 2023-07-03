package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.persistence.internal.PdbHeaderRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PdbHeaderRepository
    extends MongoRepository<PdbHeader, String>, GenericMongoRepository, PdbHeaderRepositoryCustom<PdbHeader, String>
{

}
