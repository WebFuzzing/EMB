package org.cbioportal.genome_nexus.persistence.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MyVariantInfoRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "my_variant_info.annotation";

    @Autowired
    public MyVariantInfoRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }
}
