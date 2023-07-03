package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MyVariantInfoRepository
    extends MongoRepository<MyVariantInfo, String>, GenericMongoRepository {}
