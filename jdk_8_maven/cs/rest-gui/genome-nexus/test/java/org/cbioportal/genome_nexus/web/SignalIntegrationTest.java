package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.persistence.SignalMutationRepository;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38897"
    }
)
public class SignalIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38897/signal/mutation";

    private static List<SignalMutation> SIGNAL_MUTATIONS;

    @BeforeClass
    public static void readMockFile() throws IOException
    {
        JsonToObjectMapper objectMapper = new JsonToObjectMapper();
        // reading the test DB dump
        SIGNAL_MUTATIONS = objectMapper.readSignalMutations("signal_integration_test.json");
    }


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private NotationConverter notationConverter;

    @Autowired
    private SignalMutationRepository signalMutationRepository;

    @Before
    public void setupDB()
    {
        // only init once
        if (this.signalMutationRepository.count() == 0) {
            // save the dump into the embedded mongoDB
            this.signalMutationRepository.insert(SIGNAL_MUTATIONS);
        }
    }

    private SignalMutation[] fetchSignalMutationsByHugoSymbolGET(String hugoGeneSymbol)
    {
        return this.restTemplate.getForObject(BASE_URL + "?hugoGeneSymbol=" + hugoGeneSymbol, SignalMutation[].class);
    }

    private SignalMutation[] fetchSignalMutationsByVariantGET(String hgvsg)
    {
        return this.restTemplate.getForObject(BASE_URL + "/hgvs/" + hgvsg, SignalMutation[].class);
    }

    @Test
    public void testFetchByHugoSymbol()
    {
        // genomic location strings (needed for GET requests)
        String[] hugoSymbols = {
            "INVALID", // invalid symbol
            "TP53",
            "BRCA2"
        };

        //////////////////
        // GET requests //
        //////////////////

        SignalMutation[] mutationsByInvalidSymbol = this.fetchSignalMutationsByHugoSymbolGET(hugoSymbols[0]);

        // expected to be empty
        assertEquals(0, mutationsByInvalidSymbol.length);


        SignalMutation[] tp53Mutations = this.fetchSignalMutationsByHugoSymbolGET(hugoSymbols[1]);

        // expected to be NOT empty
        assertEquals(2, tp53Mutations.length);

        SignalMutation[] brca2Mutations = this.fetchSignalMutationsByHugoSymbolGET(hugoSymbols[2]);

        // expected to be NOT empty
        assertEquals(2, brca2Mutations.length);
    }

    @Test
    public void testFetchByHgvsg()
    {
        // genomic location strings (needed for GET requests)
        String[] hgvsgIds = {
            "17:g.7574003_7574004insGAACATCTCGAAGCGCTCACGCCCAC", // TP53
            "17:g.7577018C>G",                                   // TP53
            "13:g.32893385_32893389delTATTC",                    // BRCA2
            "13:g.32893378C>T",                                  // BRCA2
            "10:g.89624230_89624231delAC"                        // PTEN
        };

        //////////////////
        // GET requests //
        //////////////////

        SignalMutation[] mutations0 = this.fetchSignalMutationsByVariantGET(hgvsgIds[0]);

        // expected to be NOT empty
        assertEquals(1, mutations0.length);

        SignalMutation[] mutations1 = this.fetchSignalMutationsByVariantGET(hgvsgIds[1]);

        // expected to be NOT empty
        assertEquals(1, mutations1.length);

        SignalMutation[] mutations2 = this.fetchSignalMutationsByVariantGET(hgvsgIds[2]);

        // expected to be NOT empty
        assertEquals(1, mutations2.length);

        SignalMutation[] mutations3 = this.fetchSignalMutationsByVariantGET(hgvsgIds[3]);

        // expected to be NOT empty
        assertEquals(1, mutations3.length);

        SignalMutation[] mutations4 = this.fetchSignalMutationsByVariantGET(hgvsgIds[4]);

        // expected to be empty (no PTEN mutations in the mock DB)
        assertEquals(0, mutations4.length);
    }
}
