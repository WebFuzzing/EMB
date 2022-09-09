package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RefSeqResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String refSeq = null;

        List<String> refseqTranscriptIds = this.resolveAll(transcriptConsequence);

        // just return the first one
        if(refseqTranscriptIds != null && refseqTranscriptIds.size() > 0) {
            refSeq = refseqTranscriptIds.get(0);
        }

        return refSeq;
    }

    @Nullable
    public List<String> resolveAll(TranscriptConsequence transcriptConsequence)
    {
        List<String> refseqTranscriptIds = null;

        if(transcriptConsequence != null)
        {
            if (transcriptConsequence.getRefseqTranscriptIds() != null)
            {
                refseqTranscriptIds = transcriptConsequence.getRefseqTranscriptIds();
            }
        }

        return refseqTranscriptIds;
    }
}
