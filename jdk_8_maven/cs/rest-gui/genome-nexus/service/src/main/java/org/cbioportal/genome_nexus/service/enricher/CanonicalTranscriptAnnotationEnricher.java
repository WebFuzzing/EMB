package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;

import java.util.ArrayList;
import java.util.List;


public class CanonicalTranscriptAnnotationEnricher extends BaseAnnotationEnricher
{
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;

    public CanonicalTranscriptAnnotationEnricher(
        String id,
        VariantAnnotationSummaryService variantAnnotationSummaryService
    ) {
        super(id);
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        VariantAnnotationSummary annotationSummary =
            this.variantAnnotationSummaryService.getAnnotationSummary(annotation);
        // For backwards compatibility transcriptConsequences should be a list
        // of one when used as enrichment
        List<TranscriptConsequenceSummary> transcriptConsequences = new ArrayList<>(1);
        TranscriptConsequenceSummary transcriptConsequenceSummary = annotationSummary.getTranscriptConsequenceSummary();
        if (transcriptConsequenceSummary != null) {
            transcriptConsequences.add(transcriptConsequenceSummary);
        }
        annotationSummary.setTranscriptConsequences(transcriptConsequences);

        annotation.setAnnotationSummary(annotationSummary);
    }
}
