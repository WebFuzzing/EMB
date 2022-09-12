package org.cbioportal.genome_nexus.persistence.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VariantAnnotationRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "vep.annotation";

    @Autowired
    public VariantAnnotationRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }
}
