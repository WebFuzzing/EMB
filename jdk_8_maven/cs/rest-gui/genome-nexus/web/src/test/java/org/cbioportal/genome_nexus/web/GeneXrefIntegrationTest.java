package org.cbioportal.genome_nexus.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.component.annotation.EntrezGeneXrefResolver;
import org.cbioportal.genome_nexus.web.mixin.GeneXrefMixin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "genexrefs.url=http://grch37.rest.ensembl.org/xrefs/id/ACCESSION?content-type=application/json",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38890"
    }
)
public class GeneXrefIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38890/ensembl/xrefs/";

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private EntrezGeneXrefResolver resolver;

    private GeneXref[] fetchGeneXrefsGET(String accession)
    {
        return this.restTemplate.getForObject(BASE_URL + "?accession=" + accession, GeneXref[].class);
    }

    @Before
    public void setupRestTemplate()
    {
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();
        mixinMap.put(GeneXref.class, GeneXrefMixin.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setMixIns(mixinMap);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        messageConverters.add(converter);

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(messageConverters);
    }

    @Test
    public void testPdbHeaders()
    {
        String[] geneAccessions = {
            "ENSG00000136999", "ENSG00000272398", "ENSG00000198695"
        };

        //////////////////
        // GET requests //
        //////////////////

        GeneXref[] xrefs0 = this.fetchGeneXrefsGET(geneAccessions[0]);
        GeneXref entrezGeneXref0 = this.resolver.resolve(Arrays.asList(xrefs0), "NOV");

        assertNotNull(entrezGeneXref0);
        assertEquals(entrezGeneXref0.getPrimaryId(), "4856");

        GeneXref[] xrefs1 = this.fetchGeneXrefsGET(geneAccessions[1]);
        GeneXref entrezGeneXref1 = this.resolver.resolve(Arrays.asList(xrefs1), "CD24");

        assertNotNull(entrezGeneXref1);
        assertEquals(entrezGeneXref1.getPrimaryId(), "100133941");

        GeneXref[] xrefs2 = this.fetchGeneXrefsGET(geneAccessions[2]);
        GeneXref entrezGeneXref2 = this.resolver.resolve(Arrays.asList(xrefs2), "ND6");

        assertNotNull(entrezGeneXref2);
        assertEquals(entrezGeneXref2.getPrimaryId(), "4541");
    }
}
