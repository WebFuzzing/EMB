package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.PdbHeader;
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
        "pdb.header_service_url=http://files.rcsb.org/header/PDB_ID.pdb",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38889"
    }
)
public class PdbHeaderIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38889/pdb/header/";

    private RestTemplate restTemplate = new RestTemplate();

    private PdbHeader fetchPdbHeaderGET(String pdbId)
    {
        return this.restTemplate.getForObject(BASE_URL + pdbId, PdbHeader.class);
    }

    private PdbHeader[] fetchPdbHeadersPOST(String[] pdbIds)
    {
        return this.restTemplate.postForObject(BASE_URL, pdbIds, PdbHeader[].class);
    }

    @Test
    public void testPdbHeaders()
    {
        String[] pdbIds = {
            "1a37",
            "1a4o",
            "3njp",
            "1yy9",
            "INVALID"
        };

        //////////////////
        // GET requests //
        //////////////////

        PdbHeader header0 = this.fetchPdbHeaderGET(pdbIds[0]);

        assertEquals("1a37", header0.getPdbId());
        assertEquals("14-3-3 protein zeta bound to ps-raf259 peptide", header0.getTitle());

        PdbHeader header1 = this.fetchPdbHeaderGET(pdbIds[1]);

        assertEquals("1a4o", header1.getPdbId());
        assertEquals("14-3-3 protein zeta isoform", header1.getTitle());

        PdbHeader header2 = this.fetchPdbHeaderGET(pdbIds[2]);

        assertEquals("3njp", header2.getPdbId());
        assertEquals("the extracellular and transmembrane domain interfaces in epidermal growth factor receptor signaling", header2.getTitle());

        PdbHeader header3 = this.fetchPdbHeaderGET(pdbIds[3]);

        assertEquals("1yy9", header3.getPdbId());
        assertEquals("structure of the extracellular domain of the epidermal growth factor receptor in complex with the fab fragment of cetuximab/erbitux/imc-c225", header3.getTitle());


        //////////////////
        // POST request //
        //////////////////

        PdbHeader[] pdbHeaders = this.fetchPdbHeadersPOST(pdbIds);

        // for each pdbId we should have one matching PdbHeader instance, except the invalid one
        assertEquals(pdbHeaders.length, pdbIds.length - 1);

        // GET and POST requests should return exact same PDB Headers
        assertEquals(pdbHeaders[0].getTitle(), header0.getTitle());
        assertEquals(pdbHeaders[1].getTitle(), header1.getTitle());
        assertEquals(pdbHeaders[2].getTitle(), header2.getTitle());
        assertEquals(pdbHeaders[3].getTitle(), header3.getTitle());
    }
}
