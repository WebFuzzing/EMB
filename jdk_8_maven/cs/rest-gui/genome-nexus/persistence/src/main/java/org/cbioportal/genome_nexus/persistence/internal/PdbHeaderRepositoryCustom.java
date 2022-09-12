package org.cbioportal.genome_nexus.persistence.internal;

import java.util.Optional;

public interface PdbHeaderRepositoryCustom<T, ID>
{
    // we need to override the default MongoRepository.findById method to avoid the default behavior
    // and fetch & convert data from the simple (generic) cache
    Optional<T> findById(ID id);
}
