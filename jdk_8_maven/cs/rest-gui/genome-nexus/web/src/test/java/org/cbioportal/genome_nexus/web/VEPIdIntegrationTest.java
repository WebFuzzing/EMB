package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
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
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38891"
    }
)
public class VEPIdIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38891/annotation/dbsnp/";

    private RestTemplate restTemplate = new RestTemplate();

    private VariantAnnotation fetchVariantAnnotationByIdGET(String variantId)
    {
        return this.restTemplate.getForObject(BASE_URL + variantId, VariantAnnotation.class);
    }

    private VariantAnnotation[] fetchVariantAnnotationsByIdsPOST(String[] variantIds)
    {
        return this.restTemplate.postForObject(BASE_URL, variantIds, VariantAnnotation[].class);
    }

    @Test
    public void testVariantAnnotationById()
    {
        String[] dbSNPIds = {
            "rs116035550",
            "rs1333049",
            "asdas"
        };

        //////////////////
        // GET requests //
        //////////////////

        VariantAnnotation ann0 = this.fetchVariantAnnotationByIdGET(dbSNPIds[0]);

        assertEquals(dbSNPIds[0], ann0.getVariant());

        //////////////////
        // POST request //
        //////////////////

        VariantAnnotation[] anns = this.fetchVariantAnnotationsByIdsPOST(dbSNPIds);

        // for each dbsnpid we should have one matching variantannotation
        // instance, except the invalid one
        assertEquals(dbSNPIds.length, anns.length);

        // GET and POST requests should return exact same dbsnp ids
        assertEquals(dbSNPIds[0], anns[0].getVariant());
        assertEquals(dbSNPIds[1], anns[1].getVariant());
    }
}
