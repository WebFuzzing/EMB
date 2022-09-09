package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.Clinvar;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.persistence.ClinvarVariantAnnotationRepository;
import org.cbioportal.genome_nexus.service.ClinvarVariantAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinvarVariantAnnotationServiceImpl implements ClinvarVariantAnnotationService {
    private final ClinvarVariantAnnotationRepository clinvarRepository;

    @Autowired
    public ClinvarVariantAnnotationServiceImpl(ClinvarVariantAnnotationRepository clinvarRepository) {
        this.clinvarRepository = clinvarRepository;
    }

    @Override
    public Clinvar getClinvarVariantAnnotationByGenomicLocation(GenomicLocation genomicLocation) {
        return this.clinvarRepository.findByChromosomeAndStartPositionAndEndPositionAndReferenceAlleleAndAlternateAllele(genomicLocation.getChromosome(), genomicLocation.getStart(), genomicLocation.getEnd(), genomicLocation.getReferenceAllele(), genomicLocation.getVariantAllele());
    }
}
