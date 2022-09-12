package org.cbioportal.genome_nexus.persistence.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MutationAssessorRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "mutation_assessor.annotation";

    @Autowired
    public MutationAssessorRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }
}
