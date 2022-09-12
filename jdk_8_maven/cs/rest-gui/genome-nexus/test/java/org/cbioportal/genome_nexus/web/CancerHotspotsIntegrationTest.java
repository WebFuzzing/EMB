package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.AggregatedHotspots;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.ProteinLocation;
import org.cbioportal.genome_nexus.persistence.HotspotRepository;
import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.web.mock.JsonToObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38888"
    }
)
public class CancerHotspotsIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38888/cancer_hotspots/genomic/";

    private static List<Hotspot> HOTSPOTS;

    @BeforeClass
    public static void readMockFile() throws IOException
    {
        JsonToObjectMapper objectMapper = new JsonToObjectMapper();
        // reading the test DB dump
        HOTSPOTS = objectMapper.readHotspots("hotspots_integration_test.json");
    }


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private NotationConverter notationConverter;

    @Autowired
    private HotspotRepository hotspotRepository;

    @Before
    public void setupDB()
    {
        // only init once
        if (this.hotspotRepository.count() == 0) {
            // save the dump into the embedded mongoDB
            this.hotspotRepository.insert(HOTSPOTS);
        }
    }

    private Hotspot[] fetchHotspotsGET(String genomicLocation)
    {
        return this.restTemplate.getForObject(BASE_URL + genomicLocation, Hotspot[].class);
    }

    private Hotspot[] fetchHotspotsByTranscriptGET(String transcriptId)
    {
        return this.restTemplate.getForObject("http://localhost:38888/cancer_hotspots/transcript/" + transcriptId, Hotspot[].class);
    }

    private AggregatedHotspots[] fetchHotspotsByTranscriptPOST(List<String> transcriptIds)
    {
        return this.restTemplate.postForObject("http://localhost:38888/cancer_hotspots/transcript", transcriptIds, AggregatedHotspots[].class);
    }

    private AggregatedHotspots[] fetchHotspotsByProteinLocationPOST(List<ProteinLocation> locations)
    {
        return this.restTemplate.postForObject("http://localhost:38888/cancer_hotspots/proteinLocations", locations, AggregatedHotspots[].class);
    }

    private AggregatedHotspots[] fetchHotspotsPOST(List<GenomicLocation> genomicLocationsInstances)
    {
        return this.restTemplate.postForObject(BASE_URL, genomicLocationsInstances, AggregatedHotspots[].class);
    }

    @Test
    public void testSingleResidueHotspots()
    {
        // genomic location strings (needed for GET requests)
        String[] genomicLocations = {
            "7,140453193,140453193,T,C", // recurrent and 3D
            "17,37880220,37880220,T,C", // recurrent
            "17,37879794,37879794,G,C", // recurrent
            "12,25398284,25398285,CC,GA", // recurrent and 3D
            "12,25380271,25380271,C,T", // 3D
            "10,89692940,89692940,C,T" // none
        };

        //////////////////
        // GET requests //
        //////////////////

        Hotspot[] hotspots0 = this.fetchHotspotsGET(genomicLocations[0]);

        // expected to be both recurrent and 3D hotspot
        assertEquals(2, hotspots0.length);
        assertEquals("N581", hotspots0[0].getResidue());
        assertEquals("BRAF", hotspots0[0].getHugoSymbol());
        assertEquals("N581", hotspots0[1].getResidue());
        assertEquals("BRAF", hotspots0[1].getHugoSymbol());


        Hotspot[] hotspots1 = this.fetchHotspotsGET(genomicLocations[1]);

        // expected to be a recurrent hotspot but not 3D
        assertEquals(1, hotspots1.length);
        assertEquals("L755", hotspots1[0].getResidue());
        assertEquals("ERBB2", hotspots1[0].getHugoSymbol());
        assertEquals("single residue", hotspots1[0].getType());

        Hotspot[] hotspots2 = this.fetchHotspotsGET(genomicLocations[2]);

        // expected to be a recurrent hotspot but not 3D
        assertEquals(1, hotspots2.length);
        assertEquals("V697", hotspots2[0].getResidue());
        assertEquals("ERBB2", hotspots2[0].getHugoSymbol());
        assertEquals("single residue", hotspots2[0].getType());

        Hotspot[] hotspots3 = this.fetchHotspotsGET(genomicLocations[3]);

        // expected to be both recurrent and 3D hotspot
        assertEquals(2, hotspots3.length);
        assertEquals("G12", hotspots3[0].getResidue());
        assertEquals("KRAS", hotspots3[0].getHugoSymbol());
        assertEquals("G12", hotspots3[1].getResidue());
        assertEquals("KRAS", hotspots3[1].getHugoSymbol());

        Hotspot[] hotspots4 = this.fetchHotspotsGET(genomicLocations[4]);

        // expected to be 3D hotspot only
        assertEquals(1, hotspots4.length);
        assertEquals("E63", hotspots4[0].getResidue());
        assertEquals("KRAS", hotspots4[0].getHugoSymbol());
        assertEquals("3d", hotspots4[0].getType());

        Hotspot[] hotspots5 = this.fetchHotspotsGET(genomicLocations[5]);

        // not a hotspot, there should me no match
        assertEquals(0, hotspots5.length);

        // genomic location instances (needed for POST requests)
        List<GenomicLocation> genomicLocationsInstances = Arrays.stream(genomicLocations).map(
            g -> this.notationConverter.parseGenomicLocation(g, ",")).collect(Collectors.toList());

        //////////////////
        // POST request //
        //////////////////

        AggregatedHotspots[] aggregatedHotspots = this.fetchHotspotsPOST(genomicLocationsInstances);

        // for each genomic location we should have one matching AggregatedHotspots instance
        assertEquals(genomicLocations.length, aggregatedHotspots.length);

        // GET and POST requests should return exact same hotspots
        assertEquals(aggregatedHotspots[0].getHotspots().size(), hotspots0.length);
        assertEquals(aggregatedHotspots[1].getHotspots().size(), hotspots1.length);
        assertEquals(aggregatedHotspots[2].getHotspots().size(), hotspots2.length);
        assertEquals(aggregatedHotspots[3].getHotspots().size(), hotspots3.length);
        assertEquals(aggregatedHotspots[4].getHotspots().size(), hotspots4.length);
        assertEquals(aggregatedHotspots[5].getHotspots().size(), hotspots5.length);

        // genomic locations should be mapped correctly for the POST request
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(0)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[0].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(1)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[1].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(2)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[2].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(3)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[3].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(4)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[4].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(5)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[5].getGenomicLocation()));
    }

    @Test
    public void testIndelHotspots()
    {
        String baseUrl = "http://localhost:38888/cancer_hotspots/genomic/";

        // genomic location strings (needed for GET requests)
        String[] genomicLocations = {
            "7,55242466,55242480,GAATTAAGAGAAGCA,-",
            "7,55249002,55249003,-,CAGCGTGGA"
        };

        //////////////////
        // GET requests //
        //////////////////

        Hotspot[] hotspots0 = this.fetchHotspotsGET(genomicLocations[0]);

        assertEquals(1, hotspots0.length);
        assertEquals("EGFR", hotspots0[0].getHugoSymbol());
        assertEquals("in-frame indel", hotspots0[0].getType());

        Hotspot[] hotspots1 = this.fetchHotspotsGET(genomicLocations[1]);

        assertEquals(1, hotspots1.length);
        assertEquals("EGFR", hotspots1[0].getHugoSymbol());
        assertEquals("in-frame indel", hotspots1[0].getType());

        // genomic location instances (needed for POST requests)
        List<GenomicLocation> genomicLocationsInstances = Arrays.stream(genomicLocations).map(
            g -> this.notationConverter.parseGenomicLocation(g, ",")).collect(Collectors.toList());

        //////////////////
        // POST request //
        //////////////////

        AggregatedHotspots[] aggregatedHotspots = restTemplate.postForObject(
            baseUrl, genomicLocationsInstances, AggregatedHotspots[].class);

        // for each genomic location we should have one matching AggregatedHotspots instance
        assertEquals(genomicLocations.length, aggregatedHotspots.length);

        // GET and POST requests should return exact same hotspots
        assertEquals(aggregatedHotspots[0].getHotspots().size(), hotspots0.length);
        assertEquals(aggregatedHotspots[1].getHotspots().size(), hotspots1.length);

        // genomic locations should be mapped correctly for the POST request
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(0)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[0].getGenomicLocation()));
        assertEquals(this.notationConverter.genomicToHgvs(genomicLocationsInstances.get(1)),
            this.notationConverter.genomicToHgvs(aggregatedHotspots[1].getGenomicLocation()));
    }

    @Test
    public void testDifferentTranscriptHotspots()
    {
        String baseUrl = "http://localhost:38888/cancer_hotspots/genomic/";

        // genomic location strings (needed for GET requests)
        String[] genomicLocations = {
            // This variant has a protein change that is the same as hotspot
            // DNMT1 E432 but it's on a different transcript than the one that
            // was used for the hotspot paper
            "19,10267171,10267171,T,C",
        };

        //////////////////
        // GET requests //
        //////////////////

        Hotspot[] hotspots0 = this.fetchHotspotsGET(genomicLocations[0]);

        assertEquals(0, hotspots0.length);

        // genomic location instances (needed for POST requests)
        List<GenomicLocation> genomicLocationsInstances = Arrays.stream(genomicLocations).map(
            g -> this.notationConverter.parseGenomicLocation(g, ",")).collect(Collectors.toList());

        //////////////////
        // POST request //
        //////////////////

        AggregatedHotspots[] aggregatedHotspots = restTemplate.postForObject(
            baseUrl, genomicLocationsInstances, AggregatedHotspots[].class);

        // for each genomic location we should have one matching AggregatedHotspots instance
        assertEquals(genomicLocations.length, aggregatedHotspots.length);

        // GET and POST requests should return exact same hotspots
        assertEquals(aggregatedHotspots[0].getHotspots().size(), hotspots0.length);
    }

    @Test
    public void testTranscriptIdHotspots()
    {
        String[] transcriptIds = {"ENST00000288602"};

        //////////////////
        // GET requests //
        //////////////////

        Hotspot[] hotspots0 = this.fetchHotspotsByTranscriptGET(transcriptIds[0]);

        assertEquals(2, hotspots0.length);
        assertEquals("BRAF", hotspots0[0].getHugoSymbol());
        assertEquals("N581", hotspots0[0].getResidue());
        assertEquals("single residue", hotspots0[0].getType());

        for (int i = 0; i < hotspots0.length; i++) {
            assertEquals(transcriptIds[0], hotspots0[i].getTranscriptId());
        }

        //////////////////
        // POST request //
        //////////////////

        AggregatedHotspots[] hotspots1 = this.fetchHotspotsByTranscriptPOST(Arrays.asList(transcriptIds));
        
        assertEquals(1, hotspots1.length);
        assertEquals("ENST00000288602", hotspots1[0].getTranscriptId());
        assertEquals(2, hotspots1[0].getHotspots().size());        

    }

    @Test
    public void testProteinLocation()
    {
        List<ProteinLocation> locations = new ArrayList<ProteinLocation>();
        locations.add(new ProteinLocation("ENST00000288602", 581, 581, "Missense_Mutation"));   

        //////////////////
        // POST requests //
        //////////////////
    
        AggregatedHotspots[] hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        
        assertEquals(1, hotspots.length);
        assertEquals(581, hotspots[0].getProteinLocation().getStart().intValue());
        assertEquals(581, hotspots[0].getProteinLocation().getEnd().intValue());


        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000349310", 17, 17, "Missense_Mutation"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("This missense mutation should be hotspot", 1, hotspots[0].getHotspots().size());

        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000349310", 17, 17, "stop_gained"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("This stop gain variant should not be hotspot", 0, hotspots[0].getHotspots().size());


        // AKT1 in-frame indel hotspot is 65-77
        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000349310", 76, 77, "In_Frame_Ins"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("In-frame insertion should be supported", 1, hotspots[0].getHotspots().size());

        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000349310", 60, 64, "In_Frame_Ins"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("This in-frame insertion should not be hotspot, it is not in the range", 0, hotspots[0].getHotspots().size());

        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000349310", 76, 80, "In_Frame_Del"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("In-frame deletion should be supported", 1, hotspots[0].getHotspots().size());

        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000349310", 78, 80, "In_Frame_Del"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("This in-frame deletion should not be hotspot, it is not in the range", 0, hotspots[0].getHotspots().size());

        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000397752", 1010, 1010, "Splice_Region"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("Splice region should be supported", 1, hotspots[0].getHotspots().size());

        locations = new ArrayList<>();
        locations.add(new ProteinLocation("ENST00000397752", 1009, 1009, "Splice_Region"));
        hotspots = this.fetchHotspotsByProteinLocationPOST(locations);
        assertEquals("The result should have one record no matter whether it is hotspot", 1, hotspots.length);
        assertEquals("This splice region is not a hotspot", 0, hotspots[0].getHotspots().size());
    }
}
