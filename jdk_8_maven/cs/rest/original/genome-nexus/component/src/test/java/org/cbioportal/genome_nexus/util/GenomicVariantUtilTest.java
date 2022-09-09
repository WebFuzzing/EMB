package org.cbioportal.genome_nexus.util;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

public class GenomicVariantUtilTest {
    @Test
    public void testHgvsSubstitutionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("7:g.140453136A>T");

        assertEquals("7", variant.getChromosome());
        assertEquals(GenomicVariant.RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 140453136, variant.getStart());
        assertEquals((Integer) 140453136,variant.getEnd());
        assertEquals("A", variant.getRef());
        assertEquals(GenomicVariant.Type.SUBSTITUTION, variant.getType());
        assertEquals("T", variant.getAlt());
    }

    @Test
    public void testHgvsInsertionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("10:g.32867861_32867862insT");

        assertEquals("10", variant.getChromosome());
        assertEquals(GenomicVariant.RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 32867861, variant.getStart());
        assertEquals((Integer) 32867862, variant.getEnd());
        assertEquals("-", variant.getRef());
        assertEquals(GenomicVariant.Type.INSERTION, variant.getType());
        assertEquals("T", variant.getAlt());
    }

    @Test
    public void testHgvsDeletionToGenomicVariantWithoutRef() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("1:g.4849848_4849857del");

        assertEquals("1", variant.getChromosome());
        assertEquals(GenomicVariant.RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 4849848, variant.getStart());
        assertEquals((Integer) 4849857, variant.getEnd());
        assertEquals("-", variant.getRef());
        assertEquals(GenomicVariant.Type.DELETION, variant.getType());
        assertEquals("-", variant.getAlt());
    }

    @Test
    public void testHgvsDeletionToGenomicVariantWithRef() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs( "10:g.89624230_89624231delAC");

        assertEquals("10", variant.getChromosome());
        assertEquals(GenomicVariant.RefType.GENOMIC, variant.getRefType());
        assertEquals(89624230, variant.getStart().intValue());
        assertEquals(89624231, variant.getEnd().intValue());
        assertEquals("AC", variant.getRef());
        assertEquals(GenomicVariant.Type.DELETION, variant.getType());
        assertEquals("-", variant.getAlt());
    }

    @Test
    public void testHgvsIndelToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("23:g.88778_88784delinsTAGATAG");

        assertEquals("23", variant.getChromosome());
        assertEquals(GenomicVariant.RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 88778, variant.getStart());
        assertEquals((Integer) 88784,variant.getEnd());
        assertEquals("XXXXXXX", variant.getRef());
        assertEquals(GenomicVariant.Type.INDEL, variant.getType());
        assertEquals("TAGATAG", variant.getAlt());
    }

    @Test
    public void testRegionSnpToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("5:140532-140532:1/C");

        assertEquals("5", variant.getChromosome());
        assertEquals(null, variant.getRefType()); // WARNING : null RefType probably should not be allowed
        assertEquals((Integer) 140532, variant.getStart());
        assertEquals((Integer) 140532, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("C", variant.getAlt());
    }

    @Test
    public void testRegionReverseSnpToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("14:19584687-19584687:-1/T");

        assertEquals("14", variant.getChromosome());
        assertEquals(null, variant.getRefType()); // WARNING : null RefType probably should not be allowed
        assertEquals((Integer) 19584687, variant.getStart());
        assertEquals((Integer) 19584687, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("T", variant.getAlt());
    }

    @Test
    public void testRegionInsertionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("1:881907-881906:1/C");

        assertEquals("1", variant.getChromosome());
        assertEquals(null, variant.getRefType()); // WARNING : null RefType probably should not be allowed
        assertEquals((Integer) 881907, variant.getStart());
        assertEquals((Integer) 881906, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("C", variant.getAlt());
    }

    @Test
    public void testRegionDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("2:946507-946511:1/-");

        assertEquals("2", variant.getChromosome());
        assertEquals(null, variant.getRefType()); // WARNING : null RefType probably should not be allowed
        assertEquals((Integer) 946507, variant.getStart());
        assertEquals((Integer) 946511, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("-", variant.getAlt());
    }

    @Test
    public void testGenomicVariantToRegion() {
        GenomicVariant variant = new GenomicVariant("5", GenomicVariant.RefType.GENOMIC, 140532, 140532, GenomicVariant.Type.SUBSTITUTION, null, "C");

        assertEquals("5:140532-140532:1/C", GenomicVariantUtil.toRegion(variant));
    }

    @Test
    public void testGenomicVariantInsertToRegion() {
        GenomicVariant variant = new GenomicVariant("5", GenomicVariant.RefType.GENOMIC, 140532, 140533, GenomicVariant.Type.INSERTION, null, "C");

        assertEquals("5:140533-140532:1/C", GenomicVariantUtil.toRegion(variant));
    }

    // TODO: add conversion test cases from hgvs delete and indel (maybe dup?)

    @Test
    public void testHgvsToGenomicVariantToRegion() {
        String region = GenomicVariantUtil.toRegion(GenomicVariantUtil.fromHgvs("12:g.25398285C>A"));

        assertEquals("12:25398285-25398285:1/A", region);
    }

    @Test
    public void testHgvsInsertToGenomicVariantToRegion() {
        String region = GenomicVariantUtil.toRegion(GenomicVariantUtil.fromHgvs("12:g.25398285_25398286insA"));

        assertEquals("12:25398286-25398285:1/A", region);
    }

    // TODO: add conversion test cases from hgvs delete and indel (maybe dup?)

    @Test
    public void testMafToGenomicVariant() {
        ArrayList<GenomicVariant> list = new ArrayList<GenomicVariant>();
        ArrayList<String> mafs = GenomicVariantUtil.getMafs("src/test/java/org/cbioportal/genome_nexus/util/minimal_example.in.maf");
        String[] key = mafs.remove(0).split("\\s");
        for (String maf : mafs) {
            list.add(GenomicVariantUtil.fromMaf(maf, key));
        }
        //3	178916927	178916939	TAGGCAACCGTGA	G
        //7	55220240	55220240	G	T

        GenomicVariant one = list.get(0);
        GenomicVariant two = list.get(1);

        assertEquals("3", one.getChromosome());
        assertEquals(null, one.getRefType()); // WARNING : null RefType probably should not be allowed
        assertEquals((Integer) 178916927, one.getStart());
        assertEquals((Integer) 178916939, one.getEnd());
        assertEquals(null, one.getType());
        assertEquals("TAGGCAACCGTGA", one.getRef());
        assertEquals("G", one.getAlt());

        assertEquals("7", two.getChromosome());
        assertEquals(null, two.getRefType()); // WARNING : null RefType probably should not be allowed
        assertEquals((Integer) 55220240, two.getStart());
        assertEquals((Integer) 55220240, two.getEnd());
        assertEquals(null, two.getType());
        assertEquals("G", two.getRef());
        assertEquals("T", two.getAlt());
    }

    @Test
    public void testProvidedReferenceAlleleFromHgvs() {
        // The tested function only extracts specified reference alleles. Position ranges are not examined for correct count.
        String NONE = "";
        LinkedHashMap<String, String> testCases = new LinkedHashMap<String, String>();
        // Substitution cases
        testCases.put("1g.1A>G", "A");
        testCases.put("1g.1T>G", "T");
        testCases.put("1g.1C>G", "C");
        testCases.put("1g.1G>G", "G");
        testCases.put("1g.1G>G ", "G"); // trailing whitespace is acceptable
        testCases.put("1g.1C>", "C"); // reference allele was specified, even if required tumor seq allele was missing
        testCases.put("1g.1AA>G", NONE); // too many nucleotides in reference allele
        testCases.put("1g.1>G", NONE); // reference allele missing
        testCases.put("1g.1>", NONE); // both alleles missing
        // Deletion-Insertion cases
        testCases.put("1g.1delinsT", NONE);
        testCases.put("1g.1delinsT ", NONE);
        testCases.put("1g.1delAinsT", "A");
        testCases.put("1g.1delAinsT ", "A");
        testCases.put("1g.1delAins", "A"); // tumor seq allele
        testCases.put("1g.1insTdelA", "A"); // although order of ins-del is invalid, the specified reference allele is returned
        testCases.put("1g.1_2delAAinsTT", "AA");
        testCases.put("1g.1_3delAAAinsT", "AAA");
        testCases.put("1g.1_2delinsTT", NONE);
        testCases.put("1g.1_2delAinsTT", "A"); // although nucleotide count does not match reference allele size, the tested function will extract the specified Reference Allele
        testCases.put("1g.1delAAinsTT", "AA"); // although nucleotide count does not match reference allele size, the tested function will extract the specified Reference Allele
        // Deletion cases
        testCases.put("1g.1del", NONE);
        testCases.put("1g.1del ", NONE);
        testCases.put("1g.1delA", "A");
        testCases.put("1g.1delA ", "A");
        testCases.put("1g.1_2delAA", "AA");
        testCases.put("1g.1_3delAAA", "AAA");
        testCases.put("1g.1_2del", NONE);
        testCases.put("1g.1_2delA", "A");
        testCases.put("1g.1delAA", "AA");
        // if supported, duplication cases would have the same rules / test cases as deletion
        testCases.forEach((hgvs, expectedValue) -> assertEquals("for test case " + hgvs, expectedValue, GenomicVariantUtil.providedReferenceAlleleFromHgvs(hgvs)));
    }

}
