package org.cbioportal.genome_nexus.model;

import java.util.List;

public class IndexSearch {

    private IndexSearchType queryType;
    private List<Index> results;

    public IndexSearchType getQueryType() {
        return queryType;
    }

    public void setQueryType(IndexSearchType queryType) {
        this.queryType = queryType;
    }

    public List<Index> getResults() {
        return results;
    }

    public void setResults(List<Index> results) {
        this.results = results;
    }
}
