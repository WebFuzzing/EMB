package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;


@Component
public class TranscriptIdResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String transcriptId = null;

        if (transcriptConsequence != null) {
            transcriptId = transcriptConsequence.getTranscriptId();
        }

        return transcriptId;
    }
}
