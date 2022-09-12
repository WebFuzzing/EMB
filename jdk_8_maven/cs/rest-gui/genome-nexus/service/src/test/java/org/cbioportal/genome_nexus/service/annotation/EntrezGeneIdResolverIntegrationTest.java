package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.config.EntrezGeneResolverIntegrationConfig;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ochoaa
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38895"
    },
    inheritLocations = false
)
@ContextConfiguration(classes=EntrezGeneResolverIntegrationConfig.class)
public class EntrezGeneIdResolverIntegrationTest {
    @Autowired
    private EnsemblService ensemblService;

    @Test
    public void testEntrezGeneIdResolver() {
        assertEquals("23269", ensemblService.getEntrezGeneIdByHugoSymbol("MGA"));
        assertEquals("8972", ensemblService.getEntrezGeneIdByHugoSymbol("MGAM"));
    }
}
