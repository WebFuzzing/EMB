package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.component.annotation.GenomicLocationResolver;
import org.cbioportal.genome_nexus.model.Clinvar;
import org.cbioportal.genome_nexus.model.ClinvarAnnotation;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.ClinvarVariantAnnotationService;

public class ClinvarVariantAnnotationEnricher extends BaseAnnotationEnricher
{
    private ClinvarVariantAnnotationService clinvarVariantAnnotationService;
    private final GenomicLocationResolver genomicLocationResolver;

    public ClinvarVariantAnnotationEnricher(
        String id,
        ClinvarVariantAnnotationService clinvarVariantAnnotationService
    ) {
        super(id);
        this.clinvarVariantAnnotationService = clinvarVariantAnnotationService;
        this.genomicLocationResolver = new GenomicLocationResolver();
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        GenomicLocation genomicLocation = this.genomicLocationResolver.resolve(annotation);
        Clinvar clinvar = this.clinvarVariantAnnotationService.getClinvarVariantAnnotationByGenomicLocation(genomicLocation);
        ClinvarAnnotation clinvarAnnotation = new ClinvarAnnotation(clinvar);
        annotation.setClinvarAnnotation(clinvarAnnotation);
    }
}
