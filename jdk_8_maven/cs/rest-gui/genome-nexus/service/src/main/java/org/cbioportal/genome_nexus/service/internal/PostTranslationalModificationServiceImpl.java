package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.PostTranslationalModificationRepository;
import org.cbioportal.genome_nexus.service.PostTranslationalModificationService;
import org.cbioportal.genome_nexus.util.Patterns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostTranslationalModificationServiceImpl implements PostTranslationalModificationService
{
    private final PostTranslationalModificationRepository postTranslationalModificationRepository;

    @Autowired
    public PostTranslationalModificationServiceImpl(
        PostTranslationalModificationRepository postTranslationalModificationRepository)
    {
        this.postTranslationalModificationRepository = postTranslationalModificationRepository;
    }

    @Override
    public List<PostTranslationalModification> getPostTranslationalModifications(TranscriptConsequence transcript,
                                                                                 VariantAnnotation annotation)
    {
        return this.getPostTranslationalModifications(transcript.getTranscriptId());
    }

    @Override
    public List<PostTranslationalModification> getPostTranslationalModifications(List<String> ensemblTranscriptIds)
    {
        return this.postTranslationalModificationRepository.findByEnsemblTranscriptIdsIn(
            Patterns.toStartsWithPatternList(ensemblTranscriptIds));
    }

    @Override
    public List<PostTranslationalModification> getPostTranslationalModifications(String ensemblTranscriptId)
    {
        return this.postTranslationalModificationRepository.findByEnsemblTranscriptIdsIn(
            Patterns.toStartsWithPatternList(ensemblTranscriptId));
    }
}
