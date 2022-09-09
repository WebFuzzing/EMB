package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38894"
    }
)
public class NucleotideContextIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38894/nucleotide_context/";

    private RestTemplate restTemplate = new RestTemplate();

    private NucleotideContext fetchAnnotationGET(String variant)
    {
        return this.restTemplate.getForObject(BASE_URL + variant, NucleotideContext.class);
    }

    private NucleotideContext[] fetchAnnotationsPOST(String[] variants)
    {
        return this.restTemplate.postForObject(BASE_URL, variants, NucleotideContext[].class);
    }

    @Test
    public void testVariantAnnotationById()
    {
        String[] variants = {
            "7:g.140453136A>T",
            "12:g.25398285C>A",
            "17:g.41242962_41242963insGA" // no context for indels
        };

        //////////////////
        // GET requests //
        //////////////////

        NucleotideContext ann0 = this.fetchAnnotationGET(variants[0]);

        assertEquals("CAC", ann0.getSeq());

        //////////////////
        // POST request //
        //////////////////

        NucleotideContext[] anns = this.fetchAnnotationsPOST(variants);

        // for each snv we should have one matching NucelotideContext instance
        // and none for the indel
        assertEquals(variants.length - 1, anns.length);

        assertEquals("CAC", anns[0].getSeq());
        assertEquals("CCA", anns[1].getSeq());
    }
}
