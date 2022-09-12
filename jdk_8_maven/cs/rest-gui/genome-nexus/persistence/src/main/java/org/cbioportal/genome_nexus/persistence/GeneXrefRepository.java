package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GeneXrefRepository extends MongoRepository<GeneXref, String> {}
