package org.cbioportal.genome_nexus.web.persistence;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.persistence.GenericMongoRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CanonicalTranscriptPerHgncRepository
    extends MongoRepository<DBObject, String>, GenericMongoRepository {}
