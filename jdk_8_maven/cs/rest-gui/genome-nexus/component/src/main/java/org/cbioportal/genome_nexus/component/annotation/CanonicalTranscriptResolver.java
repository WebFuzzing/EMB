package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CanonicalTranscriptResolver
{
    private final TranscriptConsequencePrioritizer consequencePrioritizer;

    @Autowired
    public CanonicalTranscriptResolver(TranscriptConsequencePrioritizer consequencePrioritizer)
    {
        this.consequencePrioritizer = consequencePrioritizer;
    }

    @Nullable
    public TranscriptConsequence resolve(VariantAnnotation variantAnnotation)
    {
        List<TranscriptConsequence> transcripts = Collections.emptyList();
        List<TranscriptConsequence> transcriptConsequences = variantAnnotation.getTranscriptConsequences();

        if (transcriptConsequences != null) {
            transcripts = this.findCanonicalTranscriptCandidates(transcriptConsequences);
        }

        // only one transcript marked as canonical
        if (transcripts.size() == 1) {
            return transcripts.iterator().next();
        }
        else if (transcripts.size() > 1) {
            return this.consequencePrioritizer.transcriptWithMostSevereConsequence(
                transcripts, variantAnnotation.getMostSevereConsequence());
        }
        // no transcript marked as canonical (list.size() == 0),
        // use most severe consequence to decide which one to pick among all available
        else {
            return this.consequencePrioritizer.transcriptWithMostSevereConsequence(
                variantAnnotation.getTranscriptConsequences(), variantAnnotation.getMostSevereConsequence());
        }
    }

    /**
     * If an isoform override have not updated the canonical transcript information,
     * then this will rely purely on VEP canonical assignment.
     */
    private List<TranscriptConsequence> findCanonicalTranscriptCandidates(
        List<TranscriptConsequence> transcriptConsequences
    ) {
        List<TranscriptConsequence> transcripts = new ArrayList<>();

        for (TranscriptConsequence transcript : transcriptConsequences)
        {
            if (transcript.getTranscriptId() != null &&
                transcript.getCanonical() != null &&
                transcript.getCanonical().equals("1"))
            {
                transcripts.add(transcript);
            }
        }

        return transcripts;
    }
}
