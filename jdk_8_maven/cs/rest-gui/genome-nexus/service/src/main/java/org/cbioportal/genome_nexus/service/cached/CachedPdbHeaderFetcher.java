package org.cbioportal.genome_nexus.service.cached;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.persistence.PdbHeaderRepository;
import org.cbioportal.genome_nexus.persistence.internal.PdbHeaderRepositoryImpl;
import org.cbioportal.genome_nexus.service.transformer.PdbHeaderTransformer;
import org.cbioportal.genome_nexus.service.remote.PdbHeaderDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedPdbHeaderFetcher extends BaseCachedExternalResourceFetcher<PdbHeader, PdbHeaderRepository>
{
    @Autowired
    public CachedPdbHeaderFetcher(PdbHeaderTransformer transformer,
                                  PdbHeaderRepository repository,
                                  PdbHeaderDataFetcher fetcher)
    {
        super(PdbHeaderRepositoryImpl.COLLECTION,
            repository,
            PdbHeader.class,
            fetcher,
            transformer);
    }
}
