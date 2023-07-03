package org.cbioportal.genome_nexus.web;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38892"
    }
)
public class MyVariantInfoIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38892/my_variant_info/variant/";

    private RestTemplate restTemplate = new RestTemplate();

    private Map<String, HashMap> fetchMyVariantInfoAnnotationGET(String variant)
    {
        String response = this.restTemplate.getForObject(BASE_URL + variant, String.class);
        // parse response string
        JsonParser springParser = JsonParserFactory.getJsonParser();
        // cast to Map<String, HashMap>
        Map<String, HashMap> map = (Map<String, HashMap>)(Map) springParser.parseMap(response);
        return map;
    }

    private List<Map<String, HashMap>> fetchMyVariantInfoAnnotationPOST(String[] variants)
    {
        String responses = this.restTemplate.postForObject(BASE_URL, variants, String.class);
        // parse response string
        JsonParser springParser = JsonParserFactory.getJsonParser();
        // cast to List<Map<String, HashMap>>
        List<Map<String, HashMap>> lists = (List<Map<String, HashMap>>)(List<?>) springParser.parseList(responses);
        return lists;
    }

    @Test
    public void testMyVariantInfoAnnotation()
    {
        String[] variants = {
            "7:g.140453136A>T",
            "17:g.41276045_41276046del",
            "rs12190874",
            "INVALID"
        };

        //////////////////
        // GET requests //
        //////////////////

        String chrom = this.fetchMyVariantInfoAnnotationGET(variants[0]).get("mutdb").get("chrom").toString();
        // the chrom shout be 7
        assertEquals("7", chrom);

        Object alleleNumber = ((HashMap) this.fetchMyVariantInfoAnnotationGET(variants[0]).get("gnomadExome").get("alleleNumber")).get("an");
        // the allele number should be 251260
        assertEquals(251260, alleleNumber);

        Integer variantId = (Integer)this.fetchMyVariantInfoAnnotationGET(variants[0]).get("clinVar").get("variantId");
        // test clinVar, the variantId should be 13961
        assertEquals((Integer)13961, variantId);

        String alt = this.fetchMyVariantInfoAnnotationGET(variants[2]).get("vcf").get("alt").toString();
        // the alt should be A
        assertEquals("A", alt);

        Object alleleCount = ((HashMap) this.fetchMyVariantInfoAnnotationGET(variants[2]).get("gnomadGenome").get("alleleCount")).get("ac");
        // the allele count should be 3282
        assertEquals(3282, alleleCount);

        String ref = this.fetchMyVariantInfoAnnotationGET(variants[1]).get("vcf").get("ref").toString();
        // the ref shout be ACF
        assertEquals("ACT", ref);


        //////////////////
        // POST request //
        //////////////////

        List<Map<String, HashMap>> postResponses = this.fetchMyVariantInfoAnnotationPOST(variants);

        // for each variant we should have one matching MyVariantInfo instance, except the invalid one
        assertEquals(postResponses.size(), variants.length - 1);

        String chrom0 = postResponses.get(0).get("mutdb").get("chrom").toString();
        // the Get and Post should have same result
        assertEquals(chrom, chrom0);

        Object alleleNumber0 = ((HashMap) postResponses.get(0).get("gnomadExome").get("alleleNumber")).get("an");
        assertEquals(alleleNumber, alleleNumber0);

        Integer variantId0 = (Integer)postResponses.get(0).get("clinVar").get("variantId");
        assertEquals((Integer)variantId, variantId0);

        String alt1 = postResponses.get(2).get("vcf").get("alt").toString();
        assertEquals(alt, alt1);

        Object alleleCount1 = ((HashMap) postResponses.get(2).get("gnomadGenome").get("alleleCount")).get("ac");
        assertEquals(alleleCount, alleleCount1);

        String ref2 = postResponses.get(1).get("vcf").get("ref").toString();
        assertEquals(ref, ref2);
    }


    @Test
    public void testMyVariantInfoAnnotationLargePOST()
    {
        String[] variants = {
            "chr17:g.7577566_7577567insA",
            "chr17:g.7577567A>C",
            "chr17:g.7577567_7577567del",
            "chr17:g.7577567_7577568insCACATGTAGTTGT",
            "chr17:g.7577568C>A",
            "chr17:g.7577568C>G",
            "chr17:g.7577568C>T",
            "chr17:g.7577569A>C",
            "chr17:g.7577569A>G",
            "chr17:g.7577569_7577569del",
            "chr17:g.7577569_7577572del",
            "chr17:g.7577569_7577580del",
            "chr17:g.7577570C>A",
            "chr17:g.7577570C>G",
            "chr17:g.7577570C>T",
            "chr17:g.7577571A>T",
            "chr17:g.7577571_7577572insTGTAGT",
            "chr17:g.7577572_7577572del",
            "chr17:g.7577572_7577574del",
            "chr17:g.7577573G>C",
            "chr17:g.7577573G>T",
            "chr17:g.7577573_7577573del",
            "chr17:g.7577574T>C",
            "chr17:g.7577574T>G",
            "chr17:g.7577575A>C",
            "chr17:g.7577575A>G",
            "chr17:g.7577575A>T",
            "chr17:g.7577576_7577578del",
            "chr17:g.7577576_7577582del",
            "chr17:g.7577577T>C",
            "chr17:g.7577578T>C",
            "chr17:g.7577579G>C",
            "chr17:g.7577579G>T",
            "chr17:g.7577579_7577581del",
            "chr17:g.7577580T>C",
            "chr17:g.7577580T>G",
            "chr17:g.7577581A>C",
            "chr17:g.7577581A>G",
            "chr17:g.7577581A>T",
            "chr17:g.7577581_7577595del",
            "chr17:g.7577584_7577585insCTAC",
            "chr17:g.7577585_7577590del",
            "chr17:g.7577586A>C",
            "chr17:g.7577586A>T",
            "chr17:g.7577586_7577587insTGGTGGTACAGTCAG",
            "chr17:g.7577587T>A",
            "chr17:g.7577587_7577587del",
            "chr17:g.7577588_7577595del",
            "chr17:g.7577589G>T",
            "chr17:g.7577589_7577590insT",
            "chr17:g.7577591_7577596del",
            "chr17:g.7577592_7577602del",
            "chr17:g.7577593_7577593del",
            "chr17:g.7577593_7577594del",
            "chr17:g.7577594A>T",
            "chr17:g.7577594_7577595del",
            "chr17:g.7577595C>T",
            "chr17:g.7577596_7577596del",
            "chr17:g.7577599C>G",
            "chr17:g.7577599_7577600insA",
            "chr17:g.7577604C>T",
            "chr17:g.7577608C>T",
            "chr17:g.7577609C>A",
            "chr17:g.7577609C>G",
            "chr17:g.7577609C>T",
            "chr17:g.7577610T>A",
            "chr17:g.7577610T>C",
            "chr17:g.7577610T>G",
            "chr17:g.7578098_7578176del",
            "chr17:g.7578175A>G",
            "chr17:g.7578175_7578233del",
            "chr17:g.7578176C>A",
            "chr17:g.7578176C>T",
            "chr17:g.7578177C>A",
            "chr17:g.7578177C>G",
            "chr17:g.7578179C>A",
            "chr17:g.7578181G>A",
            "chr17:g.7578181_7578181del",
            "chr17:g.7578184_7578184del",
            "chr17:g.7578184_7578187del",
            "chr17:g.7578184_7578208del",
            "chr17:g.7578188C>A",
            "chr17:g.7578189A>C",
            "chr17:g.7578189A>T",
            "chr17:g.7578190T>C",
            "chr17:g.7578190T>G",
            "chr17:g.7578191A>C",
            "chr17:g.7578191A>G",
            "chr17:g.7578191A>T",
            "chr17:g.7578192_7578192del",
            "chr17:g.7578192_7578193delinsA",
            "chr17:g.7578192_7578193insGGCACCACCACACTATGTCGAAAAGTGT",
            "chr17:g.7578193G>C",
            "chr17:g.7578194_7578195insCAC",
            "chr17:g.7578195_7578197del",
            "chr17:g.7578196A>C",
            "chr17:g.7578196_7578197insC",
            "chr17:g.7578196_7578197insCC",
            "chr17:g.7578196_7578197insCCACCACACTAT",
            "chr17:g.7578197C>A",
            "chr17:g.7578197_7578198delinsTT",
            "chr17:g.7578199A>C",
            "chr17:g.7578199_7578199del",
            "chr17:g.7578200_7578222del",
            "chr17:g.7578201_7578202del",
            "chr17:g.7578202A>C",
            "chr17:g.7578202A>T",
            "chr17:g.7578203C>A",
            "chr17:g.7578203C>G",
            "chr17:g.7578203C>T",
            "chr17:g.7578204A>C",
            "chr17:g.7578204A>T",
            "chr17:g.7578205C>A",
            "chr17:g.7578205C>G",
            "chr17:g.7578205C>T",
            "chr17:g.7578205_7578206insTATGTCGAAAA",
            "chr17:g.7578205_7578207delinsTTT",
            "chr17:g.7578206T>A",
            "chr17:g.7578206T>C",
            "chr17:g.7578206T>G",
            "chr17:g.7578206_7578207del",
            "chr17:g.7578207A>C",
            "chr17:g.7578207_7578215del",
            "chr17:g.7578208T>A",
            "chr17:g.7578208T>C",
            "chr17:g.7578208T>G",
            "chr17:g.7578208_7578208del",
            "chr17:g.7578208_7578217del",
            "chr17:g.7578208_7578219del",
            "chr17:g.7578209_7578209del",
            "chr17:g.7578210_7578210del",
            "chr17:g.7578211C>A",
            "chr17:g.7578211C>T",
            "chr17:g.7578211_7578212delinsA",
            "chr17:g.7578212G>A",
            "chr17:g.7578212G>C",
            "chr17:g.7578212_7578213insA",
            "chr17:g.7578213_7578213del",
            "chr17:g.7578213_7578214del",
            "chr17:g.7578213_7578231del",
            "chr17:g.7578215_7578227del",
            "chr17:g.7578217G>A",
            "chr17:g.7578219_7578220insT",
            "chr17:g.7578220_7578221del",
            "chr17:g.7578220_7578221insTTCTGTCATCCAAATACTCCACACG",
            "chr17:g.7578222_7578223del",
            "chr17:g.7578223C>T",
            "chr17:g.7578224T>A",
            "chr17:g.7578225G>T",
            "chr17:g.7578226T>A",
            "chr17:g.7578226_7578229del",
            "chr17:g.7578227C>T",
            "chr17:g.7578229_7578237del",
            "chr17:g.7578230_7578230del",
            "chr17:g.7578231_7578232insAAAT",
            "chr17:g.7578235T>A",
            "chr17:g.7578235T>C",
            "chr17:g.7578235T>G",
            "chr17:g.7578236A>G",
            "chr17:g.7578238_7578255del",
            "chr17:g.7578239C>A",
            "chr17:g.7578239_7578240delinsAG",
            "chr17:g.7578239_7578240insCA",
            "chr17:g.7578242C>A",
            "chr17:g.7578244_7578261del",
            "chr17:g.7578245G>A",
            "chr17:g.7578245_7578277del",
            "chr17:g.7578247A>T",
            "chr17:g.7578247_7578247del",
            "chr17:g.7578247_7578248del",
            "chr17:g.7578248_7578249insAT",
            "chr17:g.7578250_7578250del",
            "chr17:g.7578252_7578299del",
            "chr17:g.7578253C>A",
            "chr17:g.7578253C>T",
            "chr17:g.7578254C>A",
            "chr17:g.7578257C>A",
            "chr17:g.7578257_7578257del",
            "chr17:g.7578257_7578258delinsAA",
            "chr17:g.7578259A>T",
            "chr17:g.7578260C>T",
            "chr17:g.7578262C>A",
            "chr17:g.7578262C>G",
            "chr17:g.7578262C>T",
            "chr17:g.7578263G>A",
            "chr17:g.7578263_7578282del",
            "chr17:g.7578264_7578265delinsTT",
            "chr17:g.7578264_7578338delinsTT",
            "chr17:g.7578265A>C",
            "chr17:g.7578265A>G",
            "chr17:g.7578265A>T",
            "chr17:g.7578265_7578266insT",
            "chr17:g.7578266T>A",
            "chr17:g.7578266_7578266del",
            "chr17:g.7578266_7578267insA",
            "chr17:g.7578267_7578268insAG",
            "chr17:g.7578268A>C",
            "chr17:g.7578268A>G",
            "chr17:g.7578268_7578280del",
            "chr17:g.7578269G>A",
            "chr17:g.7578270A>T",
            "chr17:g.7578271T>A",
            "chr17:g.7578271T>C",
            "chr17:g.7578271T>G",
            "chr17:g.7578272G>A",
            "chr17:g.7578272G>C",
            "chr17:g.7578272G>T",
            "chr17:g.7578272_7578273delinsAA",
            "chr17:g.7578274T>A",
            "chr17:g.7578275G>A",
            "chr17:g.7578275_7578277del",
            "chr17:g.7578275_7578293del",
            "chr17:g.7578277_7578281del",
            "chr17:g.7578280G>A",
            "chr17:g.7578280_7578280del",
            "chr17:g.7578281G>A",
            "chr17:g.7578281G>T",
            "chr17:g.7578282_7578306del",
            "chr17:g.7578283_7578284insC",
            "chr17:g.7578284_7578284del",
            "chr17:g.7578284_7578288del",
            "chr17:g.7578284_7578297del",
            "chr17:g.7578284_7578310del",
            "chr17:g.7578285_7578296del",
            "chr17:g.7578286A>T",
            "chr17:g.7578286_7578286del",
            "chr17:g.7578287_7578287del",
            "chr17:g.7578289C>T",
            "chr17:g.7578289_7578290delinsTT",
            "chr17:g.7578290C>A",
            "chr17:g.7578290C>G",
            "chr17:g.7578290C>T",
            "chr17:g.7578291T>A",
            "chr17:g.7578291T>C",
            "chr17:g.7578291T>G",
            "chr17:g.7578293_7578977del",
            "chr17:g.7578313_7578425del",
            "chr17:g.7578359_7578373del",
            "chr17:g.7578359_7578375del",
            "chr17:g.7578360_7578373del",
            "chr17:g.7578362_7578385del",
            "chr17:g.7578365_7578383del",
            "chr17:g.7578370C>A",
            "chr17:g.7578370C>G",
            "chr17:g.7578370C>T",
            "chr17:g.7578371C>G",
            "chr17:g.7578371_7578382del",
            "chr17:g.7578372_7578373delinsC",
            "chr17:g.7578376_7578376del",
            "chr17:g.7578376_7578379del",
            "chr17:g.7578377_7578377del",
            "chr17:g.7578377_7578378del",
            "chr17:g.7578378A>T",
            "chr17:g.7578378_7578379insTC",
            "chr17:g.7578378_7578395del",
            "chr17:g.7578379_7578379del",
            "chr17:g.7578382G>C",
            "chr17:g.7578382_7578382del",
            "chr17:g.7578384_7578401del",
            "chr17:g.7578388C>G",
            "chr17:g.7578388C>T",
            "chr17:g.7578389G>A",
            "chr17:g.7578389_7578402delinsT",
            "chr17:g.7578390C>G",
            "chr17:g.7578392C>A",
            "chr17:g.7578392C>T",
            "chr17:g.7578393A>C",
            "chr17:g.7578393A>T",
            "chr17:g.7578393_7578405del",
            "chr17:g.7578394T>A",
            "chr17:g.7578394T>C",
            "chr17:g.7578394T>G",
            "chr17:g.7578394_7578395del",
            "chr17:g.7578394_7578396del",
            "chr17:g.7578395G>A",
            "chr17:g.7578395G>C",
            "chr17:g.7578395G>T",
            "chr17:g.7578397T>G",
            "chr17:g.7578397_7578397del",
            "chr17:g.7578397_7578398insG",
            "chr17:g.7578398G>C",
            "chr17:g.7578398_7578398del",
            "chr17:g.7578400G>C",
            "chr17:g.7578401_7578402delinsT",
            "chr17:g.7578402G>C",
            "chr17:g.7578402G>T",
            "chr17:g.7578403C>A",
            "chr17:g.7578403C>T",
            "chr17:g.7578404A>C",
            "chr17:g.7578404A>G",
            "chr17:g.7578404A>T",
            "chr17:g.7578406C>A",
            "chr17:g.7578406C>T",
            "chr17:g.7578406_7578406del",
            "chr17:g.7578407G>A",
            "chr17:g.7578407G>C",
            "chr17:g.7578407_7578412delinsCCC",
            "chr17:g.7578410T>A",
            "chr17:g.7578410T>C",
            "chr17:g.7578411_7578411del",
            "chr17:g.7578412A>C",
            "chr17:g.7578412A>G",
            "chr17:g.7578413C>A",
            "chr17:g.7578413C>G",
            "chr17:g.7578413C>T",
            "chr17:g.7578413_7578413del",
            "chr17:g.7578414_7578414del",
            "chr17:g.7578415A>T",
            "chr17:g.7578416C>A",
            "chr17:g.7578417_7578419delinsTG",
            "chr17:g.7578418T>C",
            "chr17:g.7578419C>A",
            "chr17:g.7578419_7578419del",
            "chr17:g.7578419_7578420insCGTCATGTG",
            "chr17:g.7578421G>A",
            "chr17:g.7578422_7578423insCATGTGCTG",
            "chr17:g.7578422_7578424del",
            "chr17:g.7578423_7578424insA",
            "chr17:g.7578423_7579121del",
            "chr17:g.7578424_7578425del",
            "chr17:g.7578424_7578427del",
            "chr17:g.7578427T>C",
            "chr17:g.7578427T>G",
            "chr17:g.7578428_7578431delinsACTA",
            "chr17:g.7578428_7578627del",
            "chr17:g.7578429_7578430insTG",
            "chr17:g.7578430_7578431del",
            "chr17:g.7578431G>A",
            "chr17:g.7578433G>C",
            "chr17:g.7578433G>T",
            "chr17:g.7578435C>A",
            "chr17:g.7578437G>A",
            "chr17:g.7578439_7578439del",
            "chr17:g.7578440T>A",
            "chr17:g.7578440T>C",
            "chr17:g.7578441G>T",
            "chr17:g.7578441_7578442delinsA",
            "chr17:g.7578442T>C",
            "chr17:g.7578442_7578450delinsCCG",
            "chr17:g.7578443A>C",
            "chr17:g.7578443A>G",
            "chr17:g.7578443A>T",
            "chr17:g.7578443_7578691del",
            "chr17:g.7578445A>C",
            "chr17:g.7578445A>T",
            "chr17:g.7578445_7578449delinsCTGGG",
            "chr17:g.7578446T>A",
            "chr17:g.7578447_7578447del",
            "chr17:g.7578448G>T",
            "chr17:g.7578449C>A",
            "chr17:g.7578449C>T",
            "chr17:g.7578452_7578453insGGCGCG",
            "chr17:g.7578453_7578455del",
            "chr17:g.7578453_7578458del",
            "chr17:g.7578454G>A",
            "chr17:g.7578454_7578455del",
            "chr17:g.7578454_7578457del",
            "chr17:g.7578454_7578464delinsACGC",
            "chr17:g.7578454_7578467del",
            "chr17:g.7578455C>G",
            "chr17:g.7578455_7578460del",
            "chr17:g.7578457C>A",
            "chr17:g.7578457C>G",
            "chr17:g.7578457C>T",
            "chr17:g.7578457_7578458delinsAA",
            "chr17:g.7578458G>A",
            "chr17:g.7578458G>C",
            "chr17:g.7578458_7578459insA",
            "chr17:g.7578458_7578464del",
            "chr17:g.7578460A>C",
            "chr17:g.7578460A>G",
            "chr17:g.7578461C>A",
            "chr17:g.7578461_7578473del",
            "chr17:g.7578462_7578463insC",
            "chr17:g.7578463C>A",
            "chr17:g.7578463C>G",
            "chr17:g.7578463C>T",
            "chr17:g.7578463_7578464insA",
            "chr17:g.7578464G>C",
            "chr17:g.7578464_7578464del",
            "chr17:g.7578464_7578476del",
            "chr17:g.7578465_7578470del",
            "chr17:g.7578466G>A",
            "chr17:g.7578466G>T",
            "chr17:g.7578467T>G",
            "chr17:g.7578467_7578468insG",
            "chr17:g.7578467_7578468insGCCGGGC",
            "chr17:g.7578467_7578498del",
            "chr17:g.7578469C>A",
            "chr17:g.7578469C>T",
            "chr17:g.7578469_7578469del",
            "chr17:g.7578469_7578470insCGGG",
            "chr17:g.7578469_7578470insGGG",
            "chr17:g.7578469_7578474del",
            "chr17:g.7578470_7578471delinsA",
            "chr17:g.7578470_7578471insG",
            "chr17:g.7578470_7578473del",
            "chr17:g.7578470_7578482del",
            "chr17:g.7578470_7578502del",
            "chr17:g.7578471_7578471del",
            "chr17:g.7578471_7578478del",
            "chr17:g.7578471_7578483del",
            "chr17:g.7578472G>A",
            "chr17:g.7578472_7578491del",
            "chr17:g.7578473_7578474insA",
            "chr17:g.7578473_7578474insC",
            "chr17:g.7578474_7578475insG",
            "chr17:g.7578475G>A",
            "chr17:g.7578475G>C",
            "chr17:g.7578475_7578475del",
            "chr17:g.7578475_7578476delinsTT",
            "chr17:g.7578476G>A",
            "chr17:g.7578476G>T",
            "chr17:g.7578476_7578477insGGGT",
            "chr17:g.7578478G>C",
            "chr17:g.7578478G>T",
            "chr17:g.7578478_7578479delinsAA",
            "chr17:g.7578478_7578480del",
            "chr17:g.7578479G>A",
            "chr17:g.7578479G>C",
            "chr17:g.7578479G>T",
            "chr17:g.7578480_7578488del",
            "chr17:g.7578480_7578495del",
            "chr17:g.7578481_7578481del",
            "chr17:g.7578481_7578482insT",
            "chr17:g.7578482_7578482del",
            "chr17:g.7578484_7578485insA",
            "chr17:g.7578484_7578502del",
            "chr17:g.7578485_7578485del",
            "chr17:g.7578486A>T",
            "chr17:g.7578488_7578488del",
            "chr17:g.7578490A>C",
            "chr17:g.7578491_7578491del",
            "chr17:g.7578492C>G",
            "chr17:g.7578492C>T",
            "chr17:g.7578493C>T",
            "chr17:g.7578493_7578500del",
            "chr17:g.7578494A>C",
            "chr17:g.7578495_7578507del",
            "chr17:g.7578496A>C",
            "chr17:g.7578496A>G",
            "chr17:g.7578496A>T",
            "chr17:g.7578499T>A",
            "chr17:g.7578499T>C",
            "chr17:g.7578499T>G",
            "chr17:g.7578499_7578499del",
            "chr17:g.7578500G>A",
            "chr17:g.7578500_7578500del",
            "chr17:g.7578500_7578524del",
            "chr17:g.7578501_7578502insA",
            "chr17:g.7578502A>C",
            "chr17:g.7578502A>G",
            "chr17:g.7578503C>T",
            "chr17:g.7578503_7578507del",
            "chr17:g.7578503_7578518del",
            "chr17:g.7578504_7578505insGGG",
            "chr17:g.7578505G>A",
            "chr17:g.7578505_7578505del",
            "chr17:g.7578505_7578510del",
            "chr17:g.7578507G>C",
            "chr17:g.7578507G>T",
            "chr17:g.7578508C>T",
            "chr17:g.7578508_7578535delinsG",
            "chr17:g.7578509A>C",
            "chr17:g.7578509A>G",
            "chr17:g.7578510_7578510del",
            "chr17:g.7578511G>A",
            "chr17:g.7578513C>G",
            "chr17:g.7578513_7578549del",
            "chr17:g.7578514T>G",
            "chr17:g.7578515T>A",
            "chr17:g.7578515_7578516insGGCCAGTTGGCAAAACATCTTGTTGAG",
            "chr17:g.7578516_7578516del",
            "chr17:g.7578517G>A",
            "chr17:g.7578518C>G",
            "chr17:g.7578520A>T",
            "chr17:g.7578520_7578525del",
            "chr17:g.7578521_7578521del",
            "chr17:g.7578521_7578531del",
            "chr17:g.7578522_7578522del",
            "chr17:g.7578523T>G",
            "chr17:g.7578523_7578548del",
            "chr17:g.7578524G>A",
            "chr17:g.7578524G>C",
            "chr17:g.7578525G>C",
            "chr17:g.7578525G>T",
            "chr17:g.7578526C>A",
            "chr17:g.7578526C>G",
            "chr17:g.7578526C>T",
            "chr17:g.7578526_7578527insA",
            "chr17:g.7578527A>C",
            "chr17:g.7578527A>G",
            "chr17:g.7578527A>T",
            "chr17:g.7578527_7578527del",
            "chr17:g.7578527_7578528del",
            "chr17:g.7578528A>C",
            "chr17:g.7578528A>T",
            "chr17:g.7578529A>C",
            "chr17:g.7578530A>C",
            "chr17:g.7578530A>G"
        };

        // make the initial large POST call
        List<Map<String, HashMap>> postResponses = this.fetchMyVariantInfoAnnotationPOST(variants);

        // make sure that at least 285 is annotated
        // 285 is the number of successfully annotated variants at the time this test created
        // over time as the service adds more anotations, the number may go over 285
        assertTrue(postResponses.size() >= 285);

        // now make a second POST call to make sure that we hit the cache and map the objects properly
        List<String> successfullyAnnotatedVariants = postResponses
            .stream()
            .map(r -> (String)(Object)r.get("variant"))
            .collect(Collectors.toList());

        List<Map<String, HashMap>> cachedPostResponses = this.fetchMyVariantInfoAnnotationPOST(
            successfullyAnnotatedVariants.toArray(new String[0]));

        // we expect to find all of the successfully annotated variants in the cache
        // TODO we should also test we only hit the cache without hitting the web service again
        assertEquals(successfullyAnnotatedVariants.size(), cachedPostResponses.size());
    }
}
