package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class NotationConverterTest
{
    private final NotationConverter notationConverter = new NotationConverter();

    @Test
    public void parseGenomicLocation()
    {
        assertNull("Null genomic location input string -> Null output",
            this.notationConverter.parseGenomicLocation(null));

        assertNull("Invalid genomic location input string -> Null output",
            this.notationConverter.parseGenomicLocation("6,6,6"));

        GenomicLocation location = this.notationConverter.parseGenomicLocation("4,9784947,9784948,-,AGA");

        assertEquals("4", location.getChromosome());
        assertEquals((Integer)9784947, location.getStart());
        assertEquals((Integer)9784948, location.getEnd());
        assertEquals("-", location.getReferenceAllele());
        assertEquals("AGA", location.getVariantAllele());

        GenomicLocation location2 = this.notationConverter.parseGenomicLocation("chr23,41242962,41242963,-,GA");

        assertEquals("X", location2.getChromosome());
        assertEquals((Integer)41242962, location2.getStart());
        assertEquals((Integer)41242963, location2.getEnd());
        assertEquals("-", location2.getReferenceAllele());
        assertEquals("GA", location2.getVariantAllele());

        GenomicLocation location3 = this.notationConverter.parseGenomicLocation("chr24,41242962,41242963,-,GA");

        assertEquals("Y", location3.getChromosome());
        assertEquals((Integer)41242962, location3.getStart());
        assertEquals((Integer)41242963, location3.getEnd());
        assertEquals("-", location3.getReferenceAllele());
        assertEquals("GA", location3.getVariantAllele());
    }

    @Test
    public void genomicToHgvs()
    {
        assertNull("Null genomic location input string -> Null output",
            this.notationConverter.genomicToHgvs((String)null));

        assertNull("Invalid genomic location input string -> Null output",
            this.notationConverter.genomicToHgvs("6,6,6"));

        assertEquals("4:g.9784947_9784948insAGA",
            this.notationConverter.genomicToHgvs("4,9784947,9784948,-,AGA"));

        assertEquals("3:g.14940279_14940280insCAT",
            this.notationConverter.genomicToHgvs("3,14940279,14940280,-,CAT"));

        assertEquals("16:g.9057113_9057114insCTG",
            this.notationConverter.genomicToHgvs("16,9057113,9057114,-,CTG"));

        assertEquals("13:g.28608258_28608275delinsGGGGTGGGGGGG",
            this.notationConverter.genomicToHgvs("13,28608258,28608275,CATATTCATATTCTCTGA,GGGGTGGGGGGG"));

        assertEquals("22:g.36689419_36689421del",
            this.notationConverter.genomicToHgvs("22,36689419,36689421,CCT,-"));
            
        assertEquals("X:g.41242962_41242963insGA",
            this.notationConverter.genomicToHgvs("X,41242962,41242963,-,GA"));

        assertEquals("X:g.41242962_41242963insGA",
            this.notationConverter.genomicToHgvs("chrX,41242962,41242963,-,GA"));

        assertEquals("X:g.41242962_41242963insGA",
            this.notationConverter.genomicToHgvs("chr23,41242962,41242963,-,GA"));

        assertEquals("Y:g.41242962_41242963insGA",
            this.notationConverter.genomicToHgvs("Y,41242962,41242963,-,GA"));

        assertEquals("Y:g.41242962_41242963insGA",
            this.notationConverter.genomicToHgvs("chrY,41242962,41242963,-,GA"));

        assertEquals("Y:g.41242962_41242963insGA",
            this.notationConverter.genomicToHgvs("chr24,41242962,41242963,-,GA"));

        assertEquals("3:g.14106026_14106037del",
            this.notationConverter.genomicToHgvs("3,14106026,14106037,CCAGCAGTAGCT,-"));

        assertEquals("22:g.29091840_29091841delinsCA",
            this.notationConverter.genomicToHgvs("22,29091840,29091841,TG,CA"));

        assertEquals("19:g.46141892_46141893delinsAA",
            this.notationConverter.genomicToHgvs("19,46141892,46141893,TC,AA"));

        assertEquals("11:g.62393546_62393547delinsAA",
            this.notationConverter.genomicToHgvs("11,62393546,62393547,GG,AA"));

        assertEquals("1:g.65325832_65325833insG",
            this.notationConverter.genomicToHgvs("1,65325832,65325833,-,G"));

        assertEquals("4:g.77675978_77675979insC",
            this.notationConverter.genomicToHgvs("4,77675978,77675979,-,C"));

        assertEquals("8:g.37696499_37696500insG",
            this.notationConverter.genomicToHgvs("8,37696499,37696500,-,G"));

        assertEquals("10:g.101953779del",
            this.notationConverter.genomicToHgvs("10,101953779,101953779,T,-"));

        assertEquals("6:g.137519505_137519506del",
            this.notationConverter.genomicToHgvs("6,137519505,137519506,CT,-"));

        assertEquals("3:g.114058003del",
            this.notationConverter.genomicToHgvs("3,114058003,114058003,G,-"));

        assertEquals("9:g.135797242delinsAT",
            this.notationConverter.genomicToHgvs("9,135797242,135797242,C,AT"));

        assertEquals("6:g.137519505_137519506delinsA",
            this.notationConverter.genomicToHgvs("6,137519505,137519506,CT,A"));

        assertEquals("7:g.140453136A>T",
            this.notationConverter.genomicToHgvs("7,140453136,140453136,A,T"));

        assertEquals("12:g.25398285C>A",
            this.notationConverter.genomicToHgvs("12,25398285,25398285,C,A"));

        assertEquals("Whitespace should be omitted",
            "17:g.7577121_7577122delinsA",
            this.notationConverter.genomicToHgvs(" 17 , 7577121 , 7577122 , GG , A "));
    }

    @Test
    public void genomicToEnsemblRestRegion()
    {
        assertNull("Null genomic location input string -> Null output",
            this.notationConverter.genomicToEnsemblRestRegion((String)null));

        assertNull("Invalid genomic location input string -> Null output",
            this.notationConverter.genomicToEnsemblRestRegion("6,6,6"));

        assertEquals("4:9784948-9784947:1/AGA",
            this.notationConverter.genomicToEnsemblRestRegion("4,9784947,9784948,-,AGA"));

        assertEquals("3:14940280-14940279:1/CAT",
            this.notationConverter.genomicToEnsemblRestRegion("3,14940279,14940280,-,CAT"));

        assertEquals("16:9057114-9057113:1/CTG",
            this.notationConverter.genomicToEnsemblRestRegion("16,9057113,9057114,-,CTG"));

        assertEquals("13:28608258-28608275:1/GGGGTGGGGGGG",
            this.notationConverter.genomicToEnsemblRestRegion("13,28608258,28608275,CATATTCATATTCTCTGA,GGGGTGGGGGGG"));

        assertEquals("22:36689419-36689421:1/-",
            this.notationConverter.genomicToEnsemblRestRegion("22,36689419,36689421,CCT,-"));
            
        assertEquals("X:41242963-41242962:1/GA",
            this.notationConverter.genomicToEnsemblRestRegion("X,41242962,41242963,-,GA"));

        assertEquals("X:41242963-41242962:1/GA",
            this.notationConverter.genomicToEnsemblRestRegion("chrX,41242962,41242963,-,GA"));

        assertEquals("X:41242963-41242962:1/GA",
            this.notationConverter.genomicToEnsemblRestRegion("chr23,41242962,41242963,-,GA"));

        assertEquals("Y:41242963-41242962:1/GA",
            this.notationConverter.genomicToEnsemblRestRegion("Y,41242962,41242963,-,GA"));

        assertEquals("Y:41242963-41242962:1/GA",
            this.notationConverter.genomicToEnsemblRestRegion("chrY,41242962,41242963,-,GA"));

        assertEquals("Y:41242963-41242962:1/GA",
            this.notationConverter.genomicToEnsemblRestRegion("chr24,41242962,41242963,-,GA"));

        assertEquals("3:14106026-14106037:1/-",
            this.notationConverter.genomicToEnsemblRestRegion("3,14106026,14106037,CCAGCAGTAGCT,-"));

        assertEquals("22:29091840-29091841:1/CA",
            this.notationConverter.genomicToEnsemblRestRegion("22,29091840,29091841,TG,CA"));

        assertEquals("19:46141892-46141893:1/AA",
            this.notationConverter.genomicToEnsemblRestRegion("19,46141892,46141893,TC,AA"));

        assertEquals("11:62393546-62393547:1/AA",
            this.notationConverter.genomicToEnsemblRestRegion("11,62393546,62393547,GG,AA"));

        assertEquals("1:65325833-65325832:1/G",
            this.notationConverter.genomicToEnsemblRestRegion("1,65325832,65325833,-,G"));

        assertEquals("4:77675979-77675978:1/C",
            this.notationConverter.genomicToEnsemblRestRegion("4,77675978,77675979,-,C"));

        assertEquals("8:37696500-37696499:1/G",
            this.notationConverter.genomicToEnsemblRestRegion("8,37696499,37696500,-,G"));

        assertEquals("10:101953779-101953779:1/-",
            this.notationConverter.genomicToEnsemblRestRegion("10,101953779,101953779,T,-"));

        assertEquals("6:137519505-137519506:1/-",
            this.notationConverter.genomicToEnsemblRestRegion("6,137519505,137519506,CT,-"));

        assertEquals("3:114058003-114058003:1/-",
            this.notationConverter.genomicToEnsemblRestRegion("3,114058003,114058003,G,-"));

        assertEquals("9:135797242-135797242:1/AT",
            this.notationConverter.genomicToEnsemblRestRegion("9,135797242,135797242,C,AT"));

        assertEquals("6:137519505-137519506:1/A",
            this.notationConverter.genomicToEnsemblRestRegion("6,137519505,137519506,CT,A"));

        assertEquals("7:140453136-140453136:1/T",
            this.notationConverter.genomicToEnsemblRestRegion("7,140453136,140453136,A,T"));

        assertEquals("12:25398285-25398285:1/A",
            this.notationConverter.genomicToEnsemblRestRegion("12,25398285,25398285,C,A"));

        assertEquals("Whitespace should be omitted",
            "17:7577121-7577122:1/A",
            this.notationConverter.genomicToEnsemblRestRegion(" 17 , 7577121 , 7577122 , GG , A "));
    }
}
