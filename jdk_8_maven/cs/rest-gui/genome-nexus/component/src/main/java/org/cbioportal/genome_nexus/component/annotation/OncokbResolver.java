package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.Alteration;
import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class OncokbResolver
{
    @Nullable
    public Alteration resolve(VariantAnnotationSummary annotationSummary)
    {
        Alteration alteration = null;

        if (annotationSummary != null) {
            TranscriptConsequenceSummary transcriptConsequenceSummary = annotationSummary.getTranscriptConsequenceSummary();
            if (transcriptConsequenceSummary != null) {
                alteration = new Alteration(transcriptConsequenceSummary.getHugoGeneSymbol(),
                                            Integer.parseInt(transcriptConsequenceSummary.getEntrezGeneId()),
                                            normalizeProteinChange(transcriptConsequenceSummary.getHgvspShort()),
                                            transcriptConsequenceSummary.getConsequenceTerms(),
                                            transcriptConsequenceSummary.getProteinPosition() != null ? transcriptConsequenceSummary.getProteinPosition().getStart() : null,
                                            transcriptConsequenceSummary.getProteinPosition() != null ? transcriptConsequenceSummary.getProteinPosition().getEnd() : null,
                                            // TODO tumorType is optional for the query, currently genome nexus doesn't have tumorType data
                                            null);
            }
        }

        return alteration;
    }

    private String normalizeProteinChange(String proteinChange) {
        if (proteinChange.contains("p.")) {
            return proteinChange.replace("p.", "");
        }
        return proteinChange;
    }
}