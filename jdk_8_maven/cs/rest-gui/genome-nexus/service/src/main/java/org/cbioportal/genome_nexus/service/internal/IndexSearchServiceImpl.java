package org.cbioportal.genome_nexus.service.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.Index;
import org.cbioportal.genome_nexus.model.IndexSearch;
import org.cbioportal.genome_nexus.model.IndexSearchType;
import org.cbioportal.genome_nexus.persistence.IndexRepository;
import org.cbioportal.genome_nexus.service.IndexSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexSearchServiceImpl implements IndexSearchService{
    private static final Log LOG = LogFactory.getLog(IndexSearchServiceImpl.class);

    public static final Integer DEFAULT_RETURN_SIZE = 10;
    public static final Integer QUERY_MIN_LENGTH = 2;

    private final IndexRepository indexRepository;

    @Autowired
    public IndexSearchServiceImpl(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Override
    public List<IndexSearch> search(String keyword) {
        return this.search(keyword, null);
    }

    @Override
    public List<IndexSearch> search(String keyword, Integer limit)
    {
        List<IndexSearch> queries = new ArrayList<>();

        // TODO support multiple keywords?
        String queryString = keyword.trim();

        if (queryString.length() >= QUERY_MIN_LENGTH)
        {
            if (queryString.contains("c.")) {
                // search by cdna
                if (queryString.contains(" ")) {
                    queries.addAll(this.searchByGeneAndCdna(queryString));
                }
                // search by hgvsc
                else {
                    queries.addAll(this.searchByHgvsc(queryString));
                }
            }
            else if (queryString.contains("g.")) {
                queries.addAll(this.searchByVariant(queryString));

            }
            else if (queryString.contains("p.")) {
                // search by gene + hgvspShort
                if (queryString.contains(" ")) {
                    queries.addAll(this.searchByGeneAndHgvspShort(queryString));
                }
                // search by gene + hgvsp
                else {
                    queries.addAll(this.searchByGeneAndHgvsp(queryString));
                }

            }
        }
        int returnSize = limit == null || limit < 1 ? DEFAULT_RETURN_SIZE: limit.intValue();
        int toIndex = Math.min(queries.size(), returnSize);

        return queries.size() > 0 ? queries.subList(0, toIndex): queries;
    }

    private List<IndexSearch> searchByVariant(
        String queryString
    ) {
        Index index = this.indexRepository.findByVariant(queryString);
        IndexSearch query = new IndexSearch();
        query.setQueryType(IndexSearchType.HGVSG);
        query.setResults(Arrays.asList(index));

        return Arrays.asList(query); 
    }

    private List<IndexSearch> searchByHgvsc(
        String queryString
    ) {
        List<Index> indexes = this.indexRepository.findByHgvsc(queryString);
        IndexSearch query = new IndexSearch();
        query.setQueryType(IndexSearchType.HGVSG);
        query.setResults(indexes);

        return Arrays.asList(query); 
    }

    private List<IndexSearch> searchByGeneAndHgvspShort(
        String queryString
    ) {
        String[] splitedInput = queryString.split(" ");
        String geneSymbol = splitedInput[0];
        String hgvspShort = splitedInput[1];

        List<Index> indexes = this.indexRepository.findByHugoSymbolAndHgvspShort(geneSymbol, hgvspShort);
        IndexSearch query = new IndexSearch();
        query.setQueryType(IndexSearchType.GENE_HGVSPSHORT);
        query.setResults(indexes);

        return Arrays.asList(query); 
    }

    private List<IndexSearch> searchByGeneAndCdna(
        String queryString
    ) {
        String[] splitedInput = queryString.split(" ");
        String geneSymbol = splitedInput[0];
        String cdna = splitedInput[1];

        List<Index> indexes = this.indexRepository.findByHugoSymbolAndCdna(geneSymbol, cdna);
        IndexSearch query = new IndexSearch();
        query.setQueryType(IndexSearchType.GENE_CDNA);
        query.setResults(indexes);

        return Arrays.asList(query); 
    }

    private List<IndexSearch> searchByGeneAndHgvsp(
        String queryString
    ) {
        String[] splitedInput = queryString.split(" ");
        String geneSymbol = splitedInput[0];
        String hgvsp = splitedInput[1];

        List<Index> indexes = this.indexRepository.findByHugoSymbolAndHgvsp(geneSymbol, hgvsp);
        IndexSearch query = new IndexSearch();
        query.setQueryType(IndexSearchType.GENE_HGVSP);
        query.setResults(indexes);

        return Arrays.asList(query); 
    }
}
