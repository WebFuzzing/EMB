package org.cbioportal.genome_nexus.persistence.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NucleotideContextRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "ensembl.nucleotide_context";

    @Autowired
    public NucleotideContextRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }
}
