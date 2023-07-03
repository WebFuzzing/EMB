package org.cbioportal.genome_nexus.persistence.internal;

import java.util.List;

import org.oncokb.client.CancerGene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OncokbCancerGenesListRepositoryImpl extends JsonMongoRepositoryImpl
{
    public static final String COLLECTION = "oncokb.gene";

    @Autowired
    public OncokbCancerGenesListRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }

    public List<CancerGene> getOncokbCancerGenesList() {
        return this.mongoTemplate.findAll(CancerGene.class, COLLECTION);
    }
}
