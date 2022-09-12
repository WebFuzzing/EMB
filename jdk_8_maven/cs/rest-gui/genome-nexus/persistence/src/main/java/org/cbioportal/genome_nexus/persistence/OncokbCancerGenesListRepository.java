package org.cbioportal.genome_nexus.persistence;

import java.util.List;

import org.oncokb.client.CancerGene;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface OncokbCancerGenesListRepository extends MongoRepository<CancerGene, String>, GenericMongoRepository {
        List<CancerGene> getOncokbCancerGenesList();
    }
