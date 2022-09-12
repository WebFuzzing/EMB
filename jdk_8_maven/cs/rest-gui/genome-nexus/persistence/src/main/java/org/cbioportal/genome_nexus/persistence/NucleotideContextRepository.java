
package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface NucleotideContextRepository
    extends MongoRepository<NucleotideContext, String>, GenericMongoRepository {}
