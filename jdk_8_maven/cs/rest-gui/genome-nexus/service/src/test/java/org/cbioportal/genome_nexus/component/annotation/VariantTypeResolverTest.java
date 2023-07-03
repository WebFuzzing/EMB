package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VariantTypeResolverTest
{
    @InjectMocks
    private VariantTypeResolver variantTypeResolver;

    @Mock
    private GenomicLocationResolver genomicLocationResolver;

    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void resolveVariantType() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        Mockito.when(
            this.genomicLocationResolver.resolveReferenceAllele(Mockito.any(VariantAnnotation.class))
        ).thenCallRealMethod();

        Mockito.when(
            this.genomicLocationResolver.resolveVariantAllele(Mockito.any(VariantAnnotation.class))
        ).thenCallRealMethod();

        // allele_string: "-/G" => expected: INS
        assertEquals(
            "Variant type for 1:g.65325832_65325833insG should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        // allele_string: "CCAGCAGTAGCT/-" => expected: DEL
        assertEquals(
            "Variant type for 3:g.14106026_14106037del should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("3:g.14106026_14106037del"))
        );

        // allele_string: "-/CAT" => expected: INS
        assertEquals(
            "Variant type for 3:g.14940279_14940280insCAT should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        // allele_string: "G/-" => expected: DEL
        assertEquals(
            "Variant type for 3:g.114058003del should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("3:g.114058003del"))
        );

        // allele_string: "-/AGA" => expected: INS
        assertEquals(
            "Variant type for 4:g.9784947_9784948insAGA should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        // allele_string: "-/C" => expected: INS
        assertEquals(
            "Variant type for 4:g.77675978_77675979insC should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        // allele_string: "CT/-" => expected: INS
        assertEquals(
            "Variant type for 6:g.137519505_137519506del should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("6:g.137519505_137519506del"))
        );

        // allele_string: "CT/A" => expected: INS
        assertEquals(
            "Variant type for 6:g.137519505_137519506delinsA should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        // allele_string: "A/T" => expected: SNP
        assertEquals(
            "Variant type for 7:g.140453136A>T should be SNP",
            "SNP",
            this.variantTypeResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        // allele_string: "-/G" => expected: INS
        assertEquals(
            "Variant type for 8:g.37696499_37696500insG should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        // allele_string: "C/AT" => expected: INS
        assertEquals(
            "Variant type for 9:g.135797242delinsAT should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("9:g.135797242delinsAT"))
        );

        // allele_string: "T/-" => expected: DEL
        assertEquals(
            "Variant type for 10:g.101953779del should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("10:g.101953779del"))
        );

        // allele_string: "GG/AA" => expected: DNP
        assertEquals(
            "Variant type for 11:g.62393546_62393547delinsAA should be DNP",
            "DNP",
            this.variantTypeResolver.resolve(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        // allele_string: "C/A" => expected: SNP
        assertEquals(
            "Variant type for 12:g.25398285C>A should be SNP",
            "SNP",
            this.variantTypeResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        // allele_string: "CATATTCATATTCTCTGA/GGGGTGGGGGGG" => expected: DEL
        assertEquals(
            "Variant type for 13:g.28608258_28608275del should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("13:g.28608258_28608275del"))
        );

        // allele_string: "-/CTG" => expected: DEL
        assertEquals(
            "Variant type for 16:g.9057113_9057114insCTG should be INS",
            "INS",
            this.variantTypeResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        // allele_string: "TC/AA" => expected: DNP
        assertEquals(
            "Variant type for 19:g.46141892_46141893delinsAA should be DNP",
            "DNP",
            this.variantTypeResolver.resolve(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        // allele_string: "TG/CA" => expected: DNP
        assertEquals(
            "Variant type for 22:g.29091840_29091841delinsCA should be DNP",
            "DNP",
            this.variantTypeResolver.resolve(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        // allele_string: "CCT/-" => expected: DEL
        assertEquals(
            "Variant type for 22:g.36689419_36689421del should be DEL",
            "DEL",
            this.variantTypeResolver.resolve(variantMockData.get("22:g.36689419_36689421del"))
        );
        // allele_string: "TTC/AAA" => expected: TNP
        assertEquals(
            "Variant type for 13:g.48954120_48954122delinsAAA should be TNP",
            "TNP",
            this.variantTypeResolver.resolve(variantMockData.get("13:g.48954120_48954122delinsAAA"))
        );
        // allele_string: "TGAG/AACT" => expected: ONP
        assertEquals(
            "Variant type for 10:g.8100129_8100132delinsAACT should be ONP",
            "ONP",
            this.variantTypeResolver.resolve(variantMockData.get("10:g.8100129_8100132delinsAACT"))
        );
    }
}
