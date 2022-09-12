package org.cbioportal.genome_nexus.web.persistence;

import org.cbioportal.genome_nexus.persistence.internal.JsonMongoRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BiomartTranscriptsRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "ensembl.biomart_transcripts";

    @Autowired
    public BiomartTranscriptsRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }
}
