package org.cbioportal.genome_nexus.service;

import java.util.List;

import org.cbioportal.genome_nexus.model.IndexSearch;

public interface IndexSearchService {
    List<IndexSearch> search(String keyword);
    List<IndexSearch> search(String keyword, Integer limit);
}
