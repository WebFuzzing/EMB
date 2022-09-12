package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.cbioportal.genome_nexus.service.MockData;

import java.io.IOException;
import java.util.*;

public class PtmMockData implements MockData<List<PostTranslationalModification>>
{

    @Override
    public Map<String, List<PostTranslationalModification>> generateData() throws IOException
    {
        Map<String, List<PostTranslationalModification>> mockData = new HashMap<>();

        PostTranslationalModification ptm;
        List<PostTranslationalModification> ptms;

        // mock data for variant: 7:g.140453136A>T -- transcript: ENST00000288602
        ptms = new ArrayList<>(1);

        ptm = new PostTranslationalModification();
        ptm.setUniprotEntry("BRAF_HUMAN");
        ptm.setUniprotAccession("P15056");
        ptm.setType("Acetylation");
        ptm.setEnsemblTranscriptIds(Arrays.asList("ENST00000288602.1", "ENST00000288602.2"));
        ptm.setPosition(154);

        ptms.add(ptm);

        mockData.put("ENST00000288602", ptms);

        // mock data for variant: 12:g.25398285C>A -- transcript: ENST00000256078
        ptms = new ArrayList<>(2);

        ptm = new PostTranslationalModification();
        ptm.setUniprotEntry("RASK_HUMAN");
        ptm.setUniprotAccession("P01116");
        ptm.setType("Palmitoylation");
        ptm.setEnsemblTranscriptIds(Arrays.asList("ENST00000256078.8", "ENST00000311936.7"));
        ptm.setPosition(186);

        ptms.add(ptm);

        ptm = new PostTranslationalModification();
        ptm.setUniprotEntry("RASK_HUMAN");
        ptm.setUniprotAccession("P01116");
        ptm.setType("Phosphorylation");
        ptm.setEnsemblTranscriptIds(Arrays.asList("ENST00000256078.8", "ENST00000311936.7"));
        ptm.setPosition(2);

        ptms.add(ptm);

        mockData.put("ENST00000256078", ptms);

        return mockData;
    }
}
