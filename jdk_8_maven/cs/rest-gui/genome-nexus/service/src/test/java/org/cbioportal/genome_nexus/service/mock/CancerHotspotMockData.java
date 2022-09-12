package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.IntegerRange;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancerHotspotMockData implements MockData<List<Hotspot>>
{

    @Override
    public Map<String, List<Hotspot>> generateData()
    {
        Map<String, List<Hotspot>> mockData = new HashMap<>();

        Hotspot hotspot;
        List<Hotspot> hotspots;

        // mock data for variant: 7:g.140453136A>T -- transcript: ENST00000288602
        hotspots = new ArrayList<>(2);

        hotspot = new Hotspot();
        //hotspot.setTranscriptId("ENST00000288602");
        //hotspot.setGeneId("ENSG00000157764");
        hotspot.setHugoSymbol("BRAF");
        hotspot.setResidue("V600");
        //hotspot.setAminoAcidPosition(new IntegerRange(600, 600));
        hotspot.setType("single residue");

        hotspots.add(hotspot);

        hotspot = new Hotspot();
        //hotspot.setTranscriptId("ENST00000288602");
        //hotspot.setGeneId("ENSG00000157764");
        hotspot.setHugoSymbol("BRAF");
        hotspot.setResidue("592-604");
        //hotspot.setAminoAcidPosition(new IntegerRange(592, 604));
        hotspot.setType("in-frame indel");

        hotspots.add(hotspot);

        mockData.put("ENST00000288602", hotspots);

        // mock data for variant: 12:g.25398285C>A -- transcript: ENST00000256078
        hotspots = new ArrayList<>(1);

        hotspot = new Hotspot();
        //hotspot.setTranscriptId("ENST00000256078");
        //hotspot.setGeneId("ENSG00000133703");
        hotspot.setHugoSymbol("KRAS");
        hotspot.setResidue("G12");
        //hotspot.setAminoAcidPosition(new IntegerRange(12, 12));
        hotspot.setType("single residue");

        hotspots.add(hotspot);

        mockData.put("ENST00000256078", hotspots);

        return mockData;
    }
}
