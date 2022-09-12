package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.Clinvar;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClinvarVariantAnnotationRepository extends MongoRepository<Clinvar, String> {
    Clinvar findByChromosomeAndStartPositionAndEndPositionAndReferenceAlleleAndAlternateAllele(
        String chromosome,
        Integer startPosition,
        Integer endPosition,
        String referenceAllele,
        String alternateAllele);
}