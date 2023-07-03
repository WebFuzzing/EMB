package org.cbioportal.genome_nexus.service.cached;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.persistence.MutationAssessorRepository;
import org.cbioportal.genome_nexus.persistence.internal.MutationAssessorRepositoryImpl;
import org.cbioportal.genome_nexus.service.remote.MutationAssessorDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedMutationAssessorFetcher extends BaseCachedExternalResourceFetcher<MutationAssessor, MutationAssessorRepository>
{
    @Autowired
    public CachedMutationAssessorFetcher(ExternalResourceTransformer<MutationAssessor> transformer,
                                         MutationAssessorRepository repository,
                                         MutationAssessorDataFetcher fetcher)
    {
        super(MutationAssessorRepositoryImpl.COLLECTION,
            repository,
            MutationAssessor.class,
            fetcher,
            transformer);
    }
}
