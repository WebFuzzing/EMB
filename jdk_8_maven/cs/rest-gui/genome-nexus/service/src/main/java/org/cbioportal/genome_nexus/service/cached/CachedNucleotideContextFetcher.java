package org.cbioportal.genome_nexus.service.cached;

import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.cbioportal.genome_nexus.persistence.NucleotideContextRepository;
import org.cbioportal.genome_nexus.persistence.internal.NucleotideContextRepositoryImpl;
import org.cbioportal.genome_nexus.service.remote.NucleotideContextDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedNucleotideContextFetcher extends BaseCachedExternalResourceFetcher<NucleotideContext, NucleotideContextRepository>
{
    @Autowired
    public CachedNucleotideContextFetcher(ExternalResourceTransformer<NucleotideContext> transformer,
                                         NucleotideContextRepository repository,
                                         NucleotideContextDataFetcher fetcher)
    {
        super(NucleotideContextRepositoryImpl.COLLECTION,
            repository,
            NucleotideContext.class,
            fetcher,
            transformer);
    }
}
