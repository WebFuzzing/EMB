package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.HashMap;
import java.util.Map;

public class MutationAssessorMockData implements MockData<MutationAssessor>
{
    @Override
    public Map<String, MutationAssessor> generateData()
    {
        Map<String, MutationAssessor> mockData = new HashMap<>();

        MutationAssessor mutationAssessor;

        // mock data for variant: 7,140453136,A,T
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol("BRAF");
        mutationAssessor.setHgvs("7:g.140453136A>T");
        mutationAssessor.setMappingIssue("");

        mockData.put("7,140453136,A,T", mutationAssessor);

        // mock data for variant: 12,25398285,C,A
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol("KRAS");
        mutationAssessor.setHgvs("12:g.25398285C>A");
        mutationAssessor.setMappingIssue("");

        mockData.put("12,25398285,C,A", mutationAssessor);

        // mock data for variant: INVALID
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol(null);
        mutationAssessor.setHgvs(null);
        mutationAssessor.setMappingIssue("Issue!");

        mockData.put("INVALID", mutationAssessor);

        return mockData;
    }
}
