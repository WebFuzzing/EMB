package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GenomicLocationResolverTest
{
    private GenomicLocationResolver genomicLocationResolver = new GenomicLocationResolver();

    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void resolveGenomicLocation() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        // allele_string: "-/G"
        assertEquals(
            "Genomic location for 1:g.65325832_65325833insG should be 1,65325832,65325833,-,G",
            "1,65325832,65325833,-,G",
            this.resolveGenomicLocationString(variantMockData.get("1:g.65325832_65325833insG"))
        );

        // allele_string: "CCAGCAGTAGCT/-"
        assertEquals(
            "Genomic location for 3:g.14106026_14106037del should be 3,14106026,14106037,CCAGCAGTAGCT,-",
            "3,14106026,14106037,CCAGCAGTAGCT,-",
            this.resolveGenomicLocationString(variantMockData.get("3:g.14106026_14106037del"))
        );

        // allele_string: "-/CAT"
        assertEquals(
            "Genomic location for 3:g.14940279_14940280insCAT should be 3,14940279,14940280,-,CAT",
            "3,14940279,14940280,-,CAT",
            this.resolveGenomicLocationString(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        // allele_string: "G/-"
        assertEquals(
            "Genomic location for 3:g.114058003del should be 3,114058003,114058003,G,-",
            "3,114058003,114058003,G,-",
            this.resolveGenomicLocationString(variantMockData.get("3:g.114058003del"))
        );

        // allele_string: "-/AGA"
        assertEquals(
            "Genomic location for 4:g.9784947_9784948insAGA should be 4,9784947,9784948,-,AGA",
            "4,9784947,9784948,-,AGA",
            this.resolveGenomicLocationString(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        // allele_string: "-/C"
        assertEquals(
            "Genomic location for 4:g.77675978_77675979insC should be 4,77675978,77675979,-,C",
            "4,77675978,77675979,-,C",
            this.resolveGenomicLocationString(variantMockData.get("4:g.77675978_77675979insC"))
        );

        // allele_string: "CT/-"
        assertEquals(
            "Genomic location for 6:g.137519505_137519506del should be 6,137519505,137519506,CT,-",
            "6,137519505,137519506,CT,-",
            this.resolveGenomicLocationString(variantMockData.get("6:g.137519505_137519506del"))
        );

        // allele_string: "CT/A"
        assertEquals(
            "Genomic location for 6:g.137519505_137519506delinsA should be 6,137519505,137519506,CT,A",
            "6,137519505,137519506,CT,A",
            this.resolveGenomicLocationString(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        // allele_string: "A/T"
        assertEquals(
            "Genomic location for 7:g.140453136A>T should be 7,140453136,A,T",
            "7,140453136,140453136,A,T",
            this.resolveGenomicLocationString(variantMockData.get("7:g.140453136A>T"))
        );

        // allele_string: "-/G"
        assertEquals(
            "Genomic location for 8:g.37696499_37696500insG should be 8,37696499,37696500,-,G",
            "8,37696499,37696500,-,G",
            this.resolveGenomicLocationString(variantMockData.get("8:g.37696499_37696500insG"))
        );

        // allele_string: "C/AT"
        assertEquals(
            "Genomic location for 9:g.135797242delinsAT should be 9,135797242,135797242,C,AT",
            "9,135797242,135797242,C,AT",
            this.resolveGenomicLocationString(variantMockData.get("9:g.135797242delinsAT"))
        );

        // allele_string: "T/-"
        assertEquals(
            "Genomic location for 10:g.101953779del should be 10,101953779,101953779,T,-",
            "10,101953779,101953779,T,-",
            this.resolveGenomicLocationString(variantMockData.get("10:g.101953779del"))
        );

        // allele_string: "GG/AA"
        assertEquals(
            "Genomic location for 11:g.62393546_62393547delinsAA should be 11,62393546,62393547,GG,AA",
            "11,62393546,62393547,GG,AA",
            this.resolveGenomicLocationString(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        // allele_string: "C/A"
        assertEquals(
            "Genomic location for 12:g.25398285C>A should be 12,25398285,25398285,C,A",
            "12,25398285,25398285,C,A",
            this.resolveGenomicLocationString(variantMockData.get("12:g.25398285C>A"))
        );

        // allele_string: "CATATTCATATTCTCTGA/GGGGTGGGGGGG"
        assertEquals(
            "Genomic location for 13:g.28608258_28608275del should be 13,28608258,28608275,CATATTCATATTCTCTGA,GGGGTGGGGGGG",
            "13,28608258,28608275,CATATTCATATTCTCTGA,GGGGTGGGGGGG",
            this.resolveGenomicLocationString(variantMockData.get("13:g.28608258_28608275del"))
        );

        // allele_string: "-/CTG"
        assertEquals(
            "Genomic location for 16:g.9057113_9057114insCTG should be 16,9057113,9057114,-,CTG",
            "16,9057113,9057114,-,CTG",
            this.resolveGenomicLocationString(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        // allele_string: "TC/AA"
        assertEquals(
            "Genomic location for 19:g.46141892_46141893delinsAA should be 19,46141892,46141893,TC,AA",
            "19,46141892,46141893,TC,AA",
            this.resolveGenomicLocationString(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        // allele_string: "TG/CA"
        assertEquals(
            "Genomic location for 22:g.29091840_29091841delinsCA should be 22,29091840,29091841,TG,CA",
            "22,29091840,29091841,TG,CA",
            this.resolveGenomicLocationString(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        // allele_string: "CCT/-"
        assertEquals(
            "Genomic location for 22:g.36689419_36689421del should be 22,36689419,36689421,CCT,-",
            "22,36689419,36689421,CCT,-",
            this.resolveGenomicLocationString(variantMockData.get("22:g.36689419_36689421del"))
        );
    }

    private String resolveGenomicLocationString(VariantAnnotation annotation)
    {
        GenomicLocation genomicLocation = this.genomicLocationResolver.resolve(annotation);

        return (
            genomicLocation.getChromosome() + "," +
            genomicLocation.getStart() + "," +
            genomicLocation.getEnd() + "," +
            genomicLocation.getReferenceAllele() + "," +
            genomicLocation.getVariantAllele()
        );
    }
}
