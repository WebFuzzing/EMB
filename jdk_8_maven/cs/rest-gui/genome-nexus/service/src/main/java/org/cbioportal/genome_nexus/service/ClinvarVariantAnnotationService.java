package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.Clinvar;
import org.cbioportal.genome_nexus.model.GenomicLocation;

public interface ClinvarVariantAnnotationService {
    Clinvar getClinvarVariantAnnotationByGenomicLocation(GenomicLocation genomicLocation);
}