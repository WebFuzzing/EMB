package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;


@Component
public class ExonResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String exon = null;

        if (transcriptConsequence != null) {
            exon = transcriptConsequence.getExon();
        }

        return exon;
    }
}
