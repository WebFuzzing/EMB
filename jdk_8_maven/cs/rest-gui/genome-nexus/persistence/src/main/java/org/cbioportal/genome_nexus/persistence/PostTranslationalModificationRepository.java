package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.regex.Pattern;

public interface PostTranslationalModificationRepository
    extends MongoRepository<PostTranslationalModification, String>
{
    List<PostTranslationalModification> findByEnsemblTranscriptIdsIn(List<Pattern> ensemblTranscriptIdsRegex);
}
