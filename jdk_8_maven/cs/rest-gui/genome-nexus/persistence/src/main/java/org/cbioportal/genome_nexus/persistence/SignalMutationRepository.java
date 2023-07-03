package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.SignalMutation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SignalMutationRepository extends MongoRepository<SignalMutation, String>
{
    List<SignalMutation> findByHugoGeneSymbol(String hugoGeneSymbol);
    List<SignalMutation> findByHugoGeneSymbolIn(List<String> hugoGeneSymbol);
    List<SignalMutation> findByChromosomeAndStartPositionAndEndPositionAndReferenceAlleleAndVariantAllele(
        String chromosome,
        Long startPosition,
        Long endPosition,
        String referenceAllele,
        String variantAllele
    );
    List<SignalMutation> findByChromosomeAndStartPositionAndEndPositionAndVariantAllele(
        String chromosome,
        Long startPosition,
        Long endPosition,
        String variantAllele
    );
}
