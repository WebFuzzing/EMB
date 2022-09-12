package org.cbioportal.genome_nexus.persistence;

import java.util.List;

import org.cbioportal.genome_nexus.model.Index;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IndexRepository extends MongoRepository<Index, String>, GenericMongoRepository {
    Index findByVariant(String Variant);
    List<Index> findByHugoSymbolAndHgvspShort(String hugoSymbol, String hgvspShort);
    List<Index> findByHugoSymbolAndHgvsp(String hugoSymbol, String hgvsp);
    List<Index> findByHugoSymbolAndCdna(String hugoSymbol, String cdna);
    List<Index> findByHgvsc(String hgvsc);
}
