package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;


@Component
public class CodonChangeResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String codonChange = null;

        if(transcriptConsequence != null) {
            codonChange = transcriptConsequence.getCodons();
        }

        return codonChange;
    }
}
