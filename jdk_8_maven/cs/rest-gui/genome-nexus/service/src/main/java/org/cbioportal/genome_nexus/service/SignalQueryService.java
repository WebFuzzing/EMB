package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.SignalQuery;

import java.util.List;

public interface SignalQueryService {
    List<SignalQuery> search(String keyword);
    List<SignalQuery> search(String keyword, Integer limit);
}
