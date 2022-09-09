package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.service.MockData;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GeneXrefMockData implements MockData<List<GeneXref>>
{
    private final JsonToObjectMapper objectMapper;

    public GeneXrefMockData()
    {
        this.objectMapper = new JsonToObjectMapper();
    }

    @Override
    public Map<String, List<GeneXref>> generateData() throws IOException
    {
        Map<String, List<GeneXref>> mockData = new LinkedHashMap<>();

        mockData.put("ENSG00000020181",
            this.objectMapper.readGeneXrefs("ENSG00000020181.json"));
        mockData.put("ENSG00000027697",
            this.objectMapper.readGeneXrefs("ENSG00000027697.json"));
        mockData.put("ENSG00000089597",
            this.objectMapper.readGeneXrefs("ENSG00000089597.json"));
        mockData.put("ENSG00000100345",
            this.objectMapper.readGeneXrefs("ENSG00000100345.json"));
        mockData.put("ENSG00000122025",
            this.objectMapper.readGeneXrefs("ENSG00000122025.json"));
        mockData.put("ENSG00000125746",
            this.objectMapper.readGeneXrefs("ENSG00000125746.json"));
        mockData.put("ENSG00000133703",
            this.objectMapper.readGeneXrefs("ENSG00000133703.json"));
        mockData.put("ENSG00000138771",
            this.objectMapper.readGeneXrefs("ENSG00000138771.json"));
        mockData.put("ENSG00000154783",
            this.objectMapper.readGeneXrefs("ENSG00000154783.json"));
        mockData.put("ENSG00000157764",
            this.objectMapper.readGeneXrefs("ENSG00000157764.json"));
        mockData.put("ENSG00000162434",
            this.objectMapper.readGeneXrefs("ENSG00000162434.json"));
        mockData.put("ENSG00000165699",
            this.objectMapper.readGeneXrefs("ENSG00000165699.json"));
        mockData.put("ENSG00000169676",
            this.objectMapper.readGeneXrefs("ENSG00000169676.json"));
        mockData.put("ENSG00000180438",
            this.objectMapper.readGeneXrefs("ENSG00000180438.json"));
        mockData.put("ENSG00000181722",
            this.objectMapper.readGeneXrefs("ENSG00000181722.json"));
        mockData.put("ENSG00000183765",
            this.objectMapper.readGeneXrefs("ENSG00000183765.json"));
        mockData.put("ENSG00000187555",
            this.objectMapper.readGeneXrefs("ENSG00000187555.json"));
        mockData.put("ENSG00000213341",
            this.objectMapper.readGeneXrefs("ENSG00000213341.json"));

        return mockData;
    }
}
