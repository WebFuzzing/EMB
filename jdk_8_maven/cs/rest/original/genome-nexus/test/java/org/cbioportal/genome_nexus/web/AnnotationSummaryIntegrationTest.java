package org.cbioportal.genome_nexus.web;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.web.persistence.BiomartTranscriptsRepository;
import org.cbioportal.genome_nexus.web.persistence.BiomartTranscriptsRepositoryImpl;
import org.cbioportal.genome_nexus.web.persistence.CanonicalTranscriptPerHgncRepository;
import org.cbioportal.genome_nexus.web.persistence.CanonicalTranscriptPerHgncRepositoryImpl;
import org.cbioportal.genome_nexus.web.mock.JsonToObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38898"
    }
)
public class AnnotationSummaryIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38898/annotation/summary/";

    private static List<DBObject> CANONICAL_TRANSCRIPTS;
    private static List<DBObject> BIOMART_TRANSCRIPTS;

    @BeforeClass
    public static void readMockFile() throws IOException
    {
        JsonToObjectMapper objectMapper = new JsonToObjectMapper();
        // reading the test DB dump
        CANONICAL_TRANSCRIPTS = objectMapper.readRawDbObjects("canonical_transcript_per_hgnc.json");
        BIOMART_TRANSCRIPTS = objectMapper.readRawDbObjects("biomart_transcripts.json");
    }

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CanonicalTranscriptPerHgncRepository canonicalTranscriptPerHgncRepository;

    @Autowired
    private BiomartTranscriptsRepository biomartTranscriptsRepository;

    @Before
    public void setupDB()
    {
        // only init once

        if (this.canonicalTranscriptPerHgncRepository.count() == 0) {
            // save the dump into the embedded mongoDB
            this.canonicalTranscriptPerHgncRepository.saveDBObjects(CanonicalTranscriptPerHgncRepositoryImpl.COLLECTION, CANONICAL_TRANSCRIPTS);
        }

        if (this.biomartTranscriptsRepository.count() == 0) {
            // save the dump into the embedded mongoDB
            this.biomartTranscriptsRepository.saveDBObjects(BiomartTranscriptsRepositoryImpl.COLLECTION, BIOMART_TRANSCRIPTS);
        }
    }

    private Map<String, Object> fetchAnnotationSummaryGET(String variant)
    {
        String response = this.restTemplate.getForObject(BASE_URL + variant, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return springParser.parseMap(response);
    }

    private List<Map<String, Object>> fetchAnnotationSummaryPOST(String[] variants)
    {
        String responses = this.restTemplate.postForObject(BASE_URL, variants, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return (List<Map<String, Object>>)(List<?>) springParser.parseList(responses);
    }

    @Test
    public void testCanonicalOverride()
    {
        String[] variants = {
            "9:g.21994291C>T",
        };

        //////////////////
        // GET requests //
        //////////////////

        String canonicalTranscript = this.fetchAnnotationSummaryGET(variants[0]).get("canonicalTranscriptId").toString();
        // VEP canonical (ENST00000404796) should be overridden with uniprot canonical (ENST00000428597)
        assertEquals("ENST00000428597", canonicalTranscript);

        //////////////////
        // POST request //
        //////////////////

        // for each variant we should have one matching instance
        assertEquals(variants.length, this.fetchAnnotationSummaryPOST(variants).size());

        String canonicalTranscript0 = this.fetchAnnotationSummaryPOST(variants).get(0).get("canonicalTranscriptId").toString();
        // VEP canonical (ENST00000404796) should be overridden with uniprot canonical (ENST00000428597)
        assertEquals("ENST00000428597", canonicalTranscript0);
    }
}
