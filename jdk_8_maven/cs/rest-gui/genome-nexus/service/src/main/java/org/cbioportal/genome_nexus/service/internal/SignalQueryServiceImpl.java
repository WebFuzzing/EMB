package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.search.SignalIndexBuilder;
import org.cbioportal.genome_nexus.component.search.SignalSearchEngine;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.persistence.SignalMutationRepository;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.service.SignalQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SignalQueryServiceImpl implements SignalQueryService
{
    private static final Log LOG = LogFactory.getLog(SignalQueryServiceImpl.class);

    public static final Integer DEFAULT_RETURN_SIZE = 10;
    public static final Integer QUERY_MIN_LENGTH = 2;

    private final SignalMutationRepository signalMutationRepository;
    private final VariantAnnotationRepository variantAnnotationRepository;
    private final SignalSearchEngine searchEngine;
    private final SignalIndexBuilder indexBuilder;

    private final List<SignalQuery> signalIndex;

    @Autowired
    public SignalQueryServiceImpl(
        SignalMutationRepository signalMutationRepository,
        VariantAnnotationRepository variantAnnotationRepository,
        SignalSearchEngine searchEngine,
        SignalIndexBuilder indexBuilder
    ) {
        this.signalMutationRepository = signalMutationRepository;
        this.variantAnnotationRepository = variantAnnotationRepository;
        this.searchEngine = searchEngine;
        this.indexBuilder = indexBuilder;

        LOG.info("Building Signal index");
        this.signalIndex = this.buildIndex();
        LOG.info("Finished building Signal index");
    }

    @Override
    public List<SignalQuery> search(String keyword) {
        return this.search(keyword, null);
    }

    @Override
    public List<SignalQuery> search(String keyword, Integer limit)
    {
        List<SignalQuery> queries = new ArrayList<>();

        String queryString = keyword.trim();

        if (queryString.length() >= QUERY_MIN_LENGTH)
        {
            queries.addAll(this.searchEngine.searchByHugoSymbol(this.signalIndex, queryString));
            queries.addAll(this.searchEngine.searchByRegion(this.signalIndex, queryString));

            // TODO these may not be 100% accurate because we don't use pre-annotated data yet
            queries.addAll(this.searchEngine.searchByVariant(this.signalIndex, queryString));
            queries.addAll(this.searchEngine.searchByAlteration(this.signalIndex, queryString));
        }

        int returnSize = limit == null || limit < 1 ? DEFAULT_RETURN_SIZE: limit.intValue();
        int toIndex = Math.min(queries.size(), returnSize);

        return queries.size() > 0 ? queries.subList(0, toIndex): queries;
    }

    private List<SignalQuery> buildIndex()
    {
        List<SignalMutation> mutations = this.signalMutationRepository.findAll();

        // this only works if the variant has already been annotated and
        // there is a corresponding entity in the DB.
        List<VariantAnnotation> annotations = this.variantAnnotationRepository.findByVariantIn(
            this.indexBuilder.findUniqueVariants(mutations)
        );

        return this.indexBuilder.buildQueryIndex(mutations, annotations);
    }
}
