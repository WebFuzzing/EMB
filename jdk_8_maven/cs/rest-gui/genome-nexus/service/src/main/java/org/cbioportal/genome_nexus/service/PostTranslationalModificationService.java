package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;

import java.util.List;

public interface PostTranslationalModificationService
{
    List<PostTranslationalModification> getPostTranslationalModifications(TranscriptConsequence transcriptConsequence, VariantAnnotation annotation);
    List<PostTranslationalModification> getPostTranslationalModifications(List<String> ensemblTranscriptIds);
    List<PostTranslationalModification> getPostTranslationalModifications(String ensemblTranscriptId);
}
