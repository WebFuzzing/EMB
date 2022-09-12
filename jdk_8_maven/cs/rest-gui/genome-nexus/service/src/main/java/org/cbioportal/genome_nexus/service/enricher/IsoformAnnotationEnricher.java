package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.util.IsoformOverrideSource;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class IsoformAnnotationEnricher extends BaseAnnotationEnricher
{
    String source;
    EnsemblService ensemblService;

    public IsoformAnnotationEnricher(
        String id,
        String source,
        EnsemblService ensemblService
    ) {
        super(id);
        this.source = source;
        this.ensemblService = ensemblService;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        // no transcripts to enrich, abort.
        if (annotation.getTranscriptConsequences() == null) {
            return;
        }

        // get transcript overrides for the given isoform override source
        // if no isoform override source is provided, then the default is uniprot
        Set<String> predefinedCanonicalTranscriptIds = this.ensemblService.getCanonicalTranscriptIdsBySource(
            IsoformOverrideSource.getOrDefault(this.source)
        );

        List<TranscriptConsequence> canonicalTranscriptCandidates = this.findCanonicalTranscriptCandidates(
            annotation.getTranscriptConsequences(),
            predefinedCanonicalTranscriptIds
        );

        // if at least one canonical transcript candidate is found
        // then mark all transcripts as non-canonical.
        //
        // if no override, then we should leave the transcript list intact
        // (rely on the canonical info provided by the web service in that case).
        if (canonicalTranscriptCandidates.size() > 0) {
            for (TranscriptConsequence transcript: annotation.getTranscriptConsequences()) {
                transcript.setCanonical(null);
            }
        }

        // override the canonical field for all the candidates
        for (TranscriptConsequence transcript: canonicalTranscriptCandidates) {
            transcript.setCanonical("1");
        }
    }

    /**
     * Tries to find a matching transcript consequence by using
     * the given predefined canonical assignment.
     */
    private List<TranscriptConsequence> findCanonicalTranscriptCandidates(
        List<TranscriptConsequence> transcriptConsequences,
        Set<String> predefinedCanonicalTranscriptIds
    ) {
        List<TranscriptConsequence> transcripts = Collections.emptyList();

        if (predefinedCanonicalTranscriptIds != null) {
            transcripts = transcriptConsequences
                .stream()
                .filter(t -> predefinedCanonicalTranscriptIds.contains(t.getTranscriptId()))
                .collect(Collectors.toList());
        }

        return transcripts;
    }
}
