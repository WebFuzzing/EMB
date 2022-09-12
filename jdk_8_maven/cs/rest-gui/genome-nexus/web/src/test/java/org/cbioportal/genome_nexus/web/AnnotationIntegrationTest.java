package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38896"
    }
)
public class AnnotationIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38896/annotation/";

    private final static String BASE_URL_GENOMIC_LOCATION = "http://localhost:38896/annotation/genomic/";

    private final static String ALL_ENSEMBL_FIELDS = "fields=annotation_summary,nucleotide_context";
    private final static String MY_VARIANT_INFO_FIELD = "fields=my_variant_info";

    private RestTemplate restTemplate = new RestTemplate();

    private Map<String, Object> fetchVariantAnnotationGET(String variant)
    {
        String response = this.restTemplate.getForObject(BASE_URL + variant, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return springParser.parseMap(response);
    }

    private List<Map<String, Object>> fetchVariantAnnotationPOST(String[] variants)
    {
        String responses = this.restTemplate.postForObject(BASE_URL, variants, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return (List<Map<String, Object>>)(List<?>) springParser.parseList(responses);
    }

    private Map<String, Object> fetchVariantAnnotationByGenomicLocationGET(String genomicLocation)
    {
        String response = this.restTemplate.getForObject(BASE_URL_GENOMIC_LOCATION + genomicLocation + "?" + ALL_ENSEMBL_FIELDS, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return springParser.parseMap(response);
    }

    private List<Map<String, Object>> fetchVariantAnnotationByGenomicLocationPOST(GenomicLocation[] genomicLocations)
    {
        String responses = this.restTemplate.postForObject(BASE_URL_GENOMIC_LOCATION + "?" + ALL_ENSEMBL_FIELDS, genomicLocations, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return (List<Map<String, Object>>)(List<?>) springParser.parseList(responses);
    }

    private List<Map<String, Object>> fetchVariantAnnotationWithMyVariantInfoByGenomicLocationPOST(GenomicLocation[] genomicLocations)
    {
        String responses = this.restTemplate.postForObject(BASE_URL_GENOMIC_LOCATION + "?" + MY_VARIANT_INFO_FIELD, genomicLocations, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return (List<Map<String, Object>>)(List<?>) springParser.parseList(responses);
    }

    @Test
    public void testAnnotation()
    {
        String[] variants = {
            "12:g.43130875C>G",
            "12:g.43130996C>G",
            "INVALID"
        };

        //////////////////
        // GET requests //
        //////////////////

        // test intergenic_consequences
        String variantAllele = ((HashMap)((ArrayList) this.fetchVariantAnnotationGET(variants[0]).get("intergenic_consequences")).get(0)).get("variantAllele").toString();
        // variantAllele should be G
        assertEquals("G", variantAllele);

        //////////////////
        // POST request //
        //////////////////

        // for each variant we should have one matching instance, except the invalid one
        assertEquals(variants.length, this.fetchVariantAnnotationPOST(variants).size());

        String variantAllele0 = ((HashMap)((ArrayList) this.fetchVariantAnnotationPOST(variants).get(0).get("intergenic_consequences")).get(0)).get("variantAllele").toString();
        // the Get and Post should have same result
        assertEquals(variantAllele, variantAllele0);

        Boolean validAnnotatedFlag = ((Boolean) this.fetchVariantAnnotationPOST(variants).get(1).get("successfully_annotated"));
        assertEquals(validAnnotatedFlag, true);

        Boolean invalidAnnotatedFlag = ((Boolean) this.fetchVariantAnnotationPOST(variants).get(2).get("successfully_annotated"));
        assertEquals(invalidAnnotatedFlag, false);
    }

    @Test
    public void testAnnotationMT()
    {
        String[] variants = {
            "MT:g.10360G>A",
        };

        //////////////////
        // GET requests //
        //////////////////

        String mostSevereConsequence = this.fetchVariantAnnotationGET(variants[0]).get("most_severe_consequence").toString();
        // most severe consequence for this MT variant should be missense_variant
        assertEquals("missense_variant", mostSevereConsequence);

        //////////////////
        // POST request //
        //////////////////

        // for each variant we should have one matching instance
        assertEquals(variants.length, this.fetchVariantAnnotationPOST(variants).size());

        String mostSevereConsequence0 = this.fetchVariantAnnotationPOST(variants).get(0).get("most_severe_consequence").toString();
        assertEquals("missense_variant", mostSevereConsequence0);

    }

    // THIS TEST HAS BEEN DISABLED. Several things need to be worked out here.
    // - the format of this query is erroneous. HGVS format for deletions no longer recommend specifying the referene allele nucleotides,
    //   and additionally our code base has been altered to convert these request into the recommended format before sending, so the request
    //   will be sent simply as "11:g.4967810del" which is a single nucleotide deletion. Additionally, even if the request to the ensembl
    //   rest endpoint did include the reference allele, the ensembl service itself is ignoring the reference allele and only annotates
    //   based on the genomic start and stop positions. This can be seen by making requests such as :
    //       - 11:g.4967810delT
    //       - 11:g.4967810delTT
    //       - 11:g.4967810delTTT
    //   all of these come back annotated as a single nucleotide deletion. To annotate a long deletion, the end position must also be given
    //   such as 11:g.4967810_4976423del for a 8614 nucleotide deletion. The reference allele *could* be specified in the (no longer
    //   recommended) fully specified format.
    // - if the format is adjusted to properly specify a 8614 nucleotide deletion, the ensembl vep annotation endpoint is failing to annotate
    //   this long deletion. This request: https://rest.ensembl.org/vep/human/hgvs/11:g.4967810_4976423del?content-type=application/json
    //   leads to this response : {"error":"Unable to parse HGVS notation '11:g.4967810_4976423del': Region requested must be smaller than 5kb"}
    //   so this integration test cannot be passed using the ensembl rest api endpoint (unless we choose to adopt the expectation that requests
    //   5kb or longer fail)
    // - when genome-nexus is deployed using a local instance of the vep command line tool (from repo genome-nexus-vep), queries are first
    //   converted to ensembl rest-like region format. So this query will be converted to "11:4967811-4976423:1/-". It has been verified that
    //   the ensembl public vep region endpoint will respond appropriately to this query, and our local deployment of genome-nexus-vep
    //   command line tool will also respond appropriately. However, there is currently a bug in the conversion code which instead converts
    //   the request into a request which is a zero-lengh insert at the start position: "11:4967811-4967810:1/-", so this alternate mode for
    //   querying a long deletion is not even possible.
    //@Test
    public void testLongDeletion()
    {
        String[] variants = {
            // ref allele has 8614 characters
            "11:g.4967810delTTTTTCTTGCAATATCTCAAGTTTCTTAAAGTGAAAGGGAAGGGAAGAACCAGGAGCATGCTCTTAAAGGAGAATACTATCCCTATTTGGGCAACTCTGACAGTTGTCAGGATTGAGGTGTATCTCAGAGGGTTGTGGATGGCTAGGAATCTATCAAATGACATGATCAGGAGGACTGAGGACTCCAGTACTGAGAATCCATGAATGAAGAATTCCTGGGCAAAGCAGGCATTGGATGAAATTTCAGGAGCATTGAACAGGAAGATGCTTAACACAGTGGGCAGAGATGATAAAGACAAACCCAAGTCTGACATAGCCAACATGGAAAGAAAATAGTACATGGGCTCATGCAAGGAGGGCTCTGTCTTGATGATAAAAAGAATGGTGCCATTTCCTAGAATAGCAATAAGATACATGCTGCAGATGGGGATAGAGATCCAGATGTGTGCATATTCTAGCCCTGGCATCCCAACCAAGAAGAAGGTGGTGATTTCAACATATGATGTGTTGATAATGGACATGATTCATTCATAGGGCTCTGCTATGAAAAATACAGATGCTTGTGTTGGTAAAATAGGAATATCTGTAAATTTAGTAGAAAAAAAGCAGTGTTAAAATTTGTGGCTTCTTTCAGCTTTCTTTTGAGAGAACAAAGCATTTATGTGGCTATCATCATGAAGAGAGATGGTTGGTTGCTGCTTTGGACTATAATCTGTCGTATAATACATTGGAAAAATTATTTCTGGACATGTTATTTCTATAGAGAATCCAGATTTAAATTTTGTATTTAGTTACGCATCTGATTTCATTTTTTCCTGTAAAATATTAGTAAACGCTGGTCAATTAACTGGAATCTTCTATTTCTTGCTCTCAGATCTATGTTCTGACCTTCAGTTCTATGTGATTAATTGCCAGTAATAATAGTCAAATGAAAGCTAAATAAATGAGAATACCTGGGTACTTACCAGTAAGTGGATATGTTTGTAAGCACTTTTGATAAAAGCTAAATAATTCTGATATACAAACCCAAGGTATTGTTGAACTTGTCCACCACAAAAGACTCCTCTTATATATAAGGGCTTGTCTTATTCTTTCTACCTAACCACTGGCTCTGGGATTACTGAGTTCTCTGGGGATTGCAGCAAGGCTAGATTTTGTCTGACAATAAGAATAAACATTTGCAGATGCAGTAAATCTACAGAGATGGTTGATACCAAGGAAACAAACATCTAGGTCTGTAATAAGGAACAATTTTGTTGGAGAGGTCTTTAATTGCCTGGTATTAGATAGACATGGTAAATTATCTGCAAAACTAGGATACTTCTGATGGATAGTTGGCTTGTGTATTACTCGTCTGCACAAGGCCAAGTCTAAGCAGAGATGCTGTGAGTTGATTCTGTATTCAAGGAAGTGTTGCAGGACAAACATCCTAGGACATAATGTGTTATCCTAGATTTTATTCCTGAATCCTGGATCCATCTCCCCAGACCCATTAGACATCACTAATTAAATATCCTTAAAACCAACTTGTCCAACTTATACAAGTTTTTCTCCCTTACCAAATCTTCCCTGTTTAAGGGACCTACTTCGGTAATAAGACTTTATGTTTAAGAATCTACAAGCCAAAAATCTGAGCTTCTTTGTCTTACACACAATAATGACCAATATTTCTTAGAAAATTAAATTGTTTTACTTACTGAGTTCCATTACCAGATGATGATTTGGAATCTTGAAATTTTTTAAATAATATTATCAAGTTATATCATTATCTTCATTTTACTGATGAAGAAAATTTTTCTCAAGATGAAGTCTTCTGTTTAAGATCACAGAAGTGTTGTAGGGTAGAGGTAAGATAGCAACTCAGGTCTGTTATATTACTGATAATTAAGTATATAGCACGCTATCAGGTTTGCCAAGCAATATTTAAGGAGTTATGAACTGAATGTACAGCTGGAATTGTGTATTATTTTTAGCATTCTAAACTGACTACTAGTTGATTCTAGTGGATAAGATGAAACATTTGGTCCTACAGTTGCAAATCTTTGCAACAGTGAAAATCATGCTAGTATAGAGATCCTAAGGAAAAGGCTCTTGAGGAGAAAAAAACTATATACTCACCTCTATTAGTAAATAGTCATTGGAAGGATTTCAATATTTACTTGACTAAAGAGTGGAAATATAAGAATGTATTACCATAACCTTCCCATAAATTCTTTTAAACTATGTGATGATTACTTCTTAGTTGCCAATTGCCTACTGTTTGCATCCCCAATGAAAATAAGCCCCACTTGGTTATGTTGTCAAGAATTCTCTCCTTCCACTGTTGCTTTTTCTCTTAGCTTTCTCCAGGTACTTCTTATTAAAGAGATAGCAGAAAGTTTGTGTATGTTTGTGTGTGTGAAAATTGCTTTGACAAGGACATGTGCTCATTGGCTCAATGTTCATGTGTTCATTGGCTCAATGTTCAACTTTGTGGACCAAATCAATTTGTTTCATGACAATTTAAAGTAAGCTACAGTAAGACAGGCAGTGCATTACTATCATGGTGGATGGCATATAGTAGCTTATTAAATACTTGTTGAATGAATAAATGATTGTATTTTTTAAACTGGCATCTTTTTTATCTTTATTATTATTTTTCCCATTGTGCTACGTTTGCTATGAAATAAAAATATGGGTTTTAGCAATTGTAAAAACTATTCTCAAGGATTTTTACATTGTAATCTAGTATTAATTTTCAACTTAATTTACATAATATGTTTTAAAAGTGTTATTTATGTACAAAGAATATTAATATACAGCTATAATTATAATAATAAAATGAGGCATGTATATTAATTATTAATATCTCATTCTAGAGACGTGAAGTATTGTTCTTGTAAAAAACAAAATGTTGCAAATCAGTTGAGGTTTCTCTTGGCCCCATATACTGAAATAATAGTGAAGGGGGGCAAATCATACAGATGAGATACAGCTAAAGCAGTTCCTATACAGAAAATTATAGGTTAAATTTATTTATTAGGAAATAAGAGTTGAAAAATCAAAAAATGTAACTTCTATTTCAAGAAGATAGTGAAGGAATAACAACAAAATCTCCAAAAAAATGAAGAAAATAAACTGGTAAAATCAGAAATGAATAAAATAGAAATATGATATAAAATAATGTCATCAAAGCAGAAATTTCATTCCTTAACAAGTTTTAAAAAATCCATAATTTAACAAAACTGAAAGAATAAAGAGATAAAACACTAAACTTATTTTAATGTATACCATTTGAACTAATGTATGTATACCCATTTCAGGGTATACATACCCATTTCAGGTACCCAGAATATGCCATTTGAACTAATGGCACTTATTCCGGAGTCATGAAAATTTTACATTTCAATCTTTATGGTAAGGCATTAGAATCACCCAGGGAGCTCTCAAAAGTTGTGATATTCAGGTTCACTTGATTTATGACAGTGACACAATTCTATAAGGAATAGATGGTATTTACAATAGATAGAGCAATTTAAATGCAAGTAGAAAGAAAGAAATCTTGAGTTTTATCAAAACATTAATTTGAGTTGGACAATAGGCCTAAGTATTCAAGGTAAAACAAAAAATCTTGTAGAAAAACTATGGTAATATTTTCATGATTTGGGATAACCATGGATACAACCAGGAAAAACAAATTTCATAGACAGAATTAATTAATAGAAAGCATTTCCATGGAAAGACCTCTGGGAATAAATAATATCATCATATGGAATAGTACTCAGCCTTAAAAAAAGGATGAAATTCTGTCATTTGCTACAACATGGATGGAACTGGAGAACATTATGTTAAATAAAATAAGCCAGGCACAGAAAGACAAACACTGCATGTTCTCACTTAGATGTGGAATCTAAAACAATTGAACTCATAGAAACAAAGTAGCATGATGAATACTAGATGATGGGGATGGGGGGAGTCTGGAGATGATAGTCAAAAGGTAAAAAGTTAGAAAGAATAAATTCGATTATTTTATAACAATAGCACAGCATGCTGAATATAGCTAATATTTGAGTCCTCTGTATTTCAATATTTCTAAGAGTAAATTTCTAATGTTCTCATCAAAAAAAAATATTATTATTATATATATCTGACAAATGATATGTAAAGAACTGCAAATGAATAAGTAAAATTCAGTAAAAAATAGGCAATGCACTTGACTTGCACTGTTCAATTTGCATTTGGAAAAATGCAAATTAATATCACAATGAGGTATTATATCATACCCATTAGAATGGCTAACATCAAAAAGAATAACAATCCCAAATTGTGGTGAATATGTGGAGAACTAGAATGCTTACACTTTGCTATTAGATGTGTAAATCAGTGCAAGTTCTTTGGTGACAAAGTTTAGCATTATCTTCTATAGGTACATAAATACCTACCCTATGACTTAAAATATCATTTCTAAAAAAGTTAGTTCCTGTTTCAAGCAAACACATTCTAAGAATGTCTATAGGAACTTTATCATATTAGCCAACATTTGAAAAAGACTCTAATATTCATCAACATGTGAATGGGTAAAATTGAATTAGGTATTCTGATTTAATTGACTATGATGTTAACCTGAATATCACAACTTTTGAGAGCTCCCTGGGTGATTTTAATGCCTTACCATAAAGATTGAAATGTAAAATTTTCATGACTCCAGAATAAGTGCCATTAGTTCAAATGGCATATTTCTGGGTACCTGTCTGTCTTTGATGGCTTGATTTCTCCATGAGTCACAATTACTGCTAGGTACAAAGGTACTTAATCACTGATGCAGAATAATGTATGCTTTTTAAAGTGCTACCAGTGAACTCTCTCCAGGCTGTTACATAAACTGATTTCTTAGATTATCATTTAGTGATCATTTTTTGTAAATAATATTTTTGTTATTATTGTTTCTCACTGAAGTGATTCTGCCATTATTGGCTGTTCTAGTAGATAAATACTGGGTTGTTAGGTTTCTTAAAAAATAATTTTTATGTTGTTTGTTCCTGTCTGATACCATTTCTCCAAACTTTCTGGAAAGAGAATGGCCATTTTAATATATCACAGTCACAGTGATAGGGGTTGTGGCACAGAGATTGGCATATGAACCAATTGCACCAGTCACATTAAGTTTTTATCCTTGGTATACTGATTACTTCTGCTTAGAATATTAAGGAAGCTATTAAACATTTTTCTTTTAAGTGGATCTGAGAGTGACAGTTCTCTCTCCTCTCTTCATTGAGAGAGAAAAAGTGCTCTACTGCCTATTTGTCTGCCCACGATTTAGCAAACTACGTGGGTTAGTTTTGTTCAAAACACGTATAGAAACAAAACAGAGAGCTGAAGCATGTAAGTTAGCGGTGATAACAACTTCTTTGCAGCACTTAAGTGTCCTGTAGCTCACAAGGTTCCTACAACTTTTAATTCAAGTCTGTCTGCTGTTATGATATTTTACCTCAGAATGTTTACGTTTCTTTAAGATAATATTGGGTTTGCTGTTGTTTGCCATGTTCTTCATTTCAGGAACATGTGCAAATCATTATGTTCACCATAAATCATCACCTCCCCCATAGGCATTCATGTATCTGTATTTTATTTTTCATTTTATTTTATTAAGACTGCCCAAAACCCCAAAAATACAGATTATCTTTGAGACTCTTGAATGAGACATCTATGTACAATATCCTGAAAGAATAAGATGCTTATTAAAGCCAACTAATTGCTCCTCATCTTGTTAGTGGAAATGAAGTCTTTATATTGAGCTGCCTATCCTCACCCTGAGAGAATAGGAGCTTCTGTTTCTTTCAATTAATAAAGCATGTCAACTACTGCTTTGTTGTACTACAACCCCAAACCCATATCTGTATTTGTAAGACTTTGGAAAAGGCAGATGGATATAGTTACGCACCTTGAACACAGAAGCAGTTCTTGCCCTGATCTGATAATAGGATTTCCTATACTAGGACAGGTTTGTGAAAACATATGTAGTTCTGAATTTAAAAACTAAAATGTACTCAAGATAATACTATCCTGGAGTATCAGAGATTAGGTTTGAATCCCAGCTTTTGATTTCCTCTCTGTGTGAGTTTCATAAAATCAAACTCTCAGAGCCTCAATCTCTTCAACTAAAATAAATAAGAAAAAACCCTTTATAAAAAGCTGGGTCATTGTGAAAAATAGCTTTTTTAAAATGCCTAAAAATACCTGGAAGCACATCGTGCATGTATTAGAGGTTTGAAAGCAATACTTGTATCTATTAAAATTTTCCATTAATTGGGAGGTACTGTCAGCTGTGGGCTGATTATTTTTTTTAAGCATTTGACATTCCTGGAAGGTGTTTCTTGATTTACCTGAGGATGAAGTGTAGGAGACTTGAACTGGCATGGTTGATGGAAATGACTTGATTCAACGAGTGGGTAAAATGAAAAGGCTAAAGTGGTTTTCACAATATGCAAGATTTTGAGGTCATAGTGGGAAGGAAAGTTGGACTATTTTTCAGCCACAAATCTAAAGCAATAAATTTCTCTTTGTACCATATCTTTTTTCCTGTTTTAAAAATCTTTAGCATCAATGATACGCCTGTATTTGAAATAGACAATTTAAGAAAATTGTATCCTACCATGAATAAATATTACTAAACTCTTCAGAGAGCAAGTTTTTCTACAAACATGGTCTTATTTTTAAAATCATTCATTAGCATTTATTTATTGAACTCTACTCTTCATCAGAAATGTTCTAGACATTAGGTAGGAAAAATTAAAGAAAATAGATAGGTTTCTTCAATCGCTCATAATACACTTTATTCAAGAAGTAGAAAGAAATGAAATAAGCACAAAAGTTATCTATAATTGCAAATATTAAGTATAATAAAGAATAAAGAATAAACAAGTAATAAAAGAGATAAAAGAGAGGGTCATAATACAAGCTTGGGCTAGGTAATTTATGATGAAATTAAGCCAACATTTAAAGAAAAAAGAAAAGGAATGCCTTACACAAAATTGGTGTCAAGCATTTTAACAAAAGAGTTCTGATGTGAAAACTCACAGGAAGAAAAGAGCTCTGTAAATTCAAAAACGATTCAGTAGGTCAATGTGGCTGGAGATTAATGAATATCTGATGATGTTGAACAAGTAGAAAGGGTCCAGCTCATGTAGGGTCTCGGAATATGTTCTTCCTTCGGCTTGGCGTGTTGGCTCACGCCTGTAATACCAGCACTTTGGGAGGCCAAGGCAGGCGGATCACTTGAGGTCAGGAGTTTGAGACCAACCTGGCCAACATGGTGAAACCCTGTCTCTACAAAAAATACAAAAATCAGCCAGGCGTGGTGACATGCGCCTGTAATCCTAGATACTCGGGAGGCTGAGAGGCAAGAGAATCGCTTGAACCTGGGAGGCGGAGGTTGCAGTGAGCAGAGATTGTGCTACTGCACCCCAGCTTGGGCTACAGAGCAAGATTTCGTCTCAAAAAAAATTTCTTTATTGATTTAATTGTATTTCTAAAGAAGTGAGAGTTCAAATTTAAAAGAGGCACTCATATATTCAGTTTGCCTTTTTAATTACTCTTGCCTTGCATAGCGAATCTTTTATAAGGCAGGGAACTTAGCAATATTTTACTGAAGAAATGCTTGGTGACTTCTAGGGTGGGGAAATGATGGGGCGGGGATTGGACTGAAAAACTATCTGTTGGGTACTATGCTCACTACCTGGGTCCAATACCCATGTAAAAATCCAACCCATATCAAATATAAAAGCTGGAATTTAAAAAAGATGGTGACATACTAACATGATTAAAATGGATAGAGAGACAGATATAATATAAAATTTGGGTCTAGCAGTAAAAGAACCTGGTATAGAGAGACTGAATGTAGATAACGAGGAATCCAACTTCTGTCAGAACTGTAAAAATTATCGTTTTTCTGTCTTGGTTGTAATTTGTTGTTTGTATCAGAGGTGCTATCATAAGACATTTATTTTTATTCTGCTTAACTGAAATGGGGACATAAGGCAACGTACTTCCTGGTTACAGTTTTATTCCCATTCTCAGTATCTAGAGTGAATAAAATAAGTCTGTGATAACGATAAAAATATGTGTTGATAATTCTTGGTGTCAAATATTAGGTTTCTCAAATTTACCTTAAATATCTTACCAGACATTTCCAGGTTTTCTGTCACATATGACTGTTAAATCTTCCATTGACACAATTTTGCTACAACTCTCACTCTAATCTGTTTAGTTTTTACACAATAAACAATTGGTTTCATCAGCGGAGGTACAAGTAGGAGAACATTTGCCATGAGAACATTAATGAGGGGAGAGACATGCCCGGCAAAGCGGTGGACAACGGCCAGGTTGATGATGGGCAGGTAGAAGATGATCACTGCACAGATGTGTGAAACACAAGTATTGAGAGCCTTAAGCTCCTCCTTTTTGGATGCAATTCCCGGTACAGTCTTGAGGATCAGGGTGTAAGACACAGCAATGAGAATAAAGTCTACCATAAGGCAGAGTGCTCCAAAAAAGCCATAGATAACATCAATTCTGTTGTCAGAACAGGCCAACTTCATGACATCCTGGTGGAGACAGTAGGAATGGGATAATTGGT",
        };

        //////////////////
        // GET requests //
        //////////////////

        String assemblyName = this.fetchVariantAnnotationGET(variants[0]).get("assembly_name").toString();
        assertEquals("GRCh37", assemblyName);

        //////////////////
        // POST request //
        //////////////////

        // for each variant we should have one matching instance
        assertEquals(variants.length, this.fetchVariantAnnotationPOST(variants).size());

        String assemblyName0 = this.fetchVariantAnnotationPOST(variants).get(0).get("assembly_name").toString();
        assertEquals("GRCh37", assemblyName0);

    }

    @Test
    public void testGenomicLocationSNVGET() {
        String genomicLocation = "7,140453136,140453136,A,T";

        Map<String, Object> response = this.fetchVariantAnnotationByGenomicLocationGET(genomicLocation);

        // TODO: somehow only the POST contains hgvsg but not GET We should
        // maybe do a more general test that checks for object equality between
        // GET response and POST response for all types of endpoints. This is
        // not enforced through typing because a lot of the fields are optional.
        // assertEquals(response.get("hgvsg"), "7:g.140453136A>T");


        HashMap annotationSummary = (HashMap) response.get("annotation_summary");
        // transcriptConsequenceSummary is the object most often used for MAF
        // annotation. It is a single transcript consequence that is deemed to
        // be most relevant because it is the most impactful conequence to a
        // canonical transcipt. If that is not available there are some other
        // rules to decide which one is (see code)


        // TODO: none of these fields work for GET endpoint, only works for POST
        // HashMap transcriptConsequenceSummary = (HashMap) annotationSummary.get("transcriptConsequenceSummary");
        // assertEquals(transcriptConsequenceSummary.get("transcriptId"), "ENST00000288602");
        // assertEquals(transcriptConsequenceSummary.get("hugoGeneSymbol"), "BRAF");
        // assertEquals(transcriptConsequenceSummary.get("hgvspShort"), "p.V600E");
    }

    @Test
    public void testGenomicLocationSNVPOST() {
        String genomicLocation = "7,140453136,140453136,A,T";

        GenomicLocation[] genomicLocations = {
            genomicLocationStringToGenomicLocation(genomicLocation)
        };

        List<Map<String, Object>> response = this.fetchVariantAnnotationByGenomicLocationPOST(genomicLocations);
        assertEquals(response.get(0).get("hgvsg"), "7:g.140453136A>T");
        HashMap annotationSummary = (HashMap) response.get(0).get("annotation_summary");
        HashMap transcriptConsequenceSummary = (HashMap) annotationSummary.get("transcriptConsequenceSummary");
        assertEquals(transcriptConsequenceSummary.get("transcriptId"), "ENST00000288602");
        assertEquals(transcriptConsequenceSummary.get("hugoGeneSymbol"), "BRAF");
        assertEquals(transcriptConsequenceSummary.get("hgvspShort"), "p.V600E");
    }

    @Test
    public void testGenomicLocationMyVariantInfoPOST() {
        String[] genomicLocationStrings = {
            "10,89624245,89624245,G,T", // valid variant
            "10.0,89624242,89624243,AA,-" // variant with invalid chromosome value
        };

        GenomicLocation[] genomicLocations = Arrays
            .stream(genomicLocationStrings)
            .map(this::genomicLocationStringToGenomicLocation)
            .collect(Collectors.toList())
            .toArray(new GenomicLocation[0]);

        List<Map<String, Object>> response =
            this.fetchVariantAnnotationWithMyVariantInfoByGenomicLocationPOST(genomicLocations);
        assertEquals("response size should be equal to request size", response.size(), 2);

        assertEquals(
            "variant (10,89624245,89624245,G,T) should be successfully annotated",
            true,
            response.get(0).get("successfully_annotated")
        );
        assertEquals(
            "variant (10,89624245,89624245,G,T) should be properly enriched with my variant info",
            "chr10:g.89624245G>T",
            ((HashMap) ((HashMap) response.get(0).get("my_variant_info")).get("annotation")).get("variant")
        );
        assertEquals(
            "variant with invalid chromosome value (10.0) should NOT be successfully annotated",
            false,
            response.get(1).get("successfully_annotated")
        );
    }

    @Test
    public void testGenomicLocationNucleotideContextGET() {
        String genomicLocation = "7,140453136,140453136,A,T";

        Map<String, Object> response = this.fetchVariantAnnotationByGenomicLocationGET(genomicLocation);
        HashMap nucleotideContext = (HashMap) response.get("nucleotide_context");
        HashMap annotation = (HashMap) nucleotideContext.get("annotation");
        String seq = (String) annotation.get("seq");
        assertEquals("CAC", seq);
    }

    @Test
    public void testGenomicLocationNucleotideContextPOST() {
        String genomicLocation = "7,140453136,140453136,A,T";
        GenomicLocation[] genomicLocations = {
            genomicLocationStringToGenomicLocation(genomicLocation)
        };
        List<Map<String, Object>> response = this.fetchVariantAnnotationByGenomicLocationPOST(genomicLocations);

        HashMap nucleotideContext = (HashMap) response.get(0).get("nucleotide_context");
        HashMap annotation = (HashMap) nucleotideContext.get("annotation");
        String seq = (String) annotation.get("seq");
        assertEquals("CAC", seq);
    }

    @Test
    public void testIdenticalGenomicLocationsPOST() {
        List<GenomicLocation> genomicLocations = new ArrayList<>();
        // If input has two identical genomic locations but in different representations
        // Return should have two objects too
        genomicLocations.add(genomicLocationStringToGenomicLocation("12,25398284,25398284,C,A"));
        genomicLocations.add(genomicLocationStringToGenomicLocation("12,25398283,25398284,AC,AA"));
        List<Map<String, Object>> response = this.fetchVariantAnnotationByGenomicLocationPOST(genomicLocations.toArray(new GenomicLocation[0]));
        assertEquals(2, response.size());
    }

    @Test
    public void testVariantAnnotationOriginalQuery() {
        String expectedConvertedVariant = "4:g.55152096_55152107del";
        String genomicLocationString = "4,55152095,55152107,ATCATGCATGATT,A";
        GenomicLocation[] genomicLocations = {
            genomicLocationStringToGenomicLocation(genomicLocationString)
        };
        List<Map<String, Object>> response = this.fetchVariantAnnotationByGenomicLocationPOST(genomicLocations);
        assertEquals(genomicLocationString, response.get(0).get("originalVariantQuery"));
        assertEquals(expectedConvertedVariant, response.get(0).get("variant"));
    }

    private GenomicLocation genomicLocationStringToGenomicLocation(String genomicLocation) {
        return new GenomicLocation() {{
            setChromosome(genomicLocation.split(",")[0]);
            setStart(Integer.parseInt(genomicLocation.split(",")[1]));
            setEnd(Integer.parseInt(genomicLocation.split(",")[2]));
            setReferenceAllele(genomicLocation.split(",")[3]);
            setVariantAllele(genomicLocation.split(",")[4]);
            setOriginalInput(genomicLocation);
        }};
    }
}
