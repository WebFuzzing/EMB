package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.cbioportal.genome_nexus.persistence.PostTranslationalModificationRepository;
import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.web.mock.JsonToObjectMapper;
import org.cbioportal.genome_nexus.web.param.PtmFilter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38893"
    }
)
public class PtmIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38893/ptm/experimental/";

    private static List<PostTranslationalModification> PTMS;

    @BeforeClass
    public static void readMockFile() throws IOException
    {
        JsonToObjectMapper objectMapper = new JsonToObjectMapper();
        // reading the test DB mock
        PTMS = objectMapper.readPtms("ptm_integration_test.json");
    }


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private NotationConverter notationConverter;

    @Autowired
    private PostTranslationalModificationRepository ptmRepository;

    @Before
    public void setupDB()
    {
        // only init once
        if (this.ptmRepository.count() == 0) {
            // save the mock into the embedded mongoDB
            this.ptmRepository.insert(PTMS);
        }
    }

    private PostTranslationalModification[] fetchPtmsGET(String ensemblTranscriptId)
    {
        return this.restTemplate.getForObject(BASE_URL + "?ensemblTranscriptId=" + ensemblTranscriptId,
            PostTranslationalModification[].class);
    }

    private PostTranslationalModification[] fetchPtmsPOST(List<String> ensemblTranscriptIds)
    {
        PtmFilter filter = new PtmFilter();
        filter.setTranscriptIds(ensemblTranscriptIds);

        return this.restTemplate.postForObject(BASE_URL, filter, PostTranslationalModification[].class);
    }

    @Test
    public void testPtmAnnotation()
    {
        String[] transcriptIds = {"ENST00000420316", "ENST00000646891", "ENST00000371953"};

        //////////////////
        // GET requests //
        //////////////////

        PostTranslationalModification[] egfrPtms = this.fetchPtmsGET(transcriptIds[0]);

        assertEquals(3, egfrPtms.length);
        assertEquals("Ubiquitination", egfrPtms[0].getType());
        assertEquals((Integer)708, egfrPtms[0].getPosition());
        assertEquals("Phosphorylation", egfrPtms[1].getType());
        assertEquals((Integer)77, egfrPtms[1].getPosition());
        assertEquals("Acetylation", egfrPtms[2].getType());
        assertEquals((Integer)1188, egfrPtms[2].getPosition());

        PostTranslationalModification[] brafPtms = this.fetchPtmsGET(transcriptIds[1]);

        assertEquals(2, brafPtms.length);
        assertEquals("Acetylation", brafPtms[0].getType());
        assertEquals((Integer)253, brafPtms[0].getPosition());
        assertEquals("Phosphorylation", brafPtms[1].getType());
        assertEquals((Integer)363, brafPtms[1].getPosition());

        PostTranslationalModification[] ptenPtms = this.fetchPtmsGET(transcriptIds[2]);

        assertEquals(1, ptenPtms.length);
        assertEquals("Ubiquitination", ptenPtms[0].getType());
        assertEquals((Integer)13, ptenPtms[0].getPosition());


        //////////////////
        // POST request //
        //////////////////

        PostTranslationalModification[] allPtms = this.fetchPtmsPOST(Arrays.asList(transcriptIds));

        assertEquals(6, allPtms.length);
    }
}
