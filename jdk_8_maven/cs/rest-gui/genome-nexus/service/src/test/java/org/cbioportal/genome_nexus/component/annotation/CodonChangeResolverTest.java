package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.CanonicalTranscriptResolverMocker;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CodonChangeResolverTest
{
    @InjectMocks
    private CodonChangeResolver codonChangeResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveCodonChangeForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "ccg/ccCg",
            this.resolveForCanonical(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "cCCAGCAGTAGCTcc/ccc",
            this.resolveForCanonical(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "cat/cCATat",
            this.resolveForCanonical(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "cCt/ct",
            this.resolveForCanonical(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "ggt/gAGAgt",
            this.resolveForCanonical(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "gcc/gCcc",
            this.resolveForCanonical(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "AGt/t",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "AGt/Tt",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "gTg/gAg",
            this.resolveForCanonical(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "gtg/gtGg",
            this.resolveForCanonical(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "atG/atAT",
            this.resolveForCanonical(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "Agg/gg",
            this.resolveForCanonical(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "ctCCag/ctTTag",
            this.resolveForCanonical(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "Ggt/Tgt",
            this.resolveForCanonical(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "tTCAGAGAATATGAATATGat/tCCCCCCCACCCCat",
            this.resolveForCanonical(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "cag/caCAGg",
            this.resolveForCanonical(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertNull(
            this.resolveForCanonical(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "tcCAag/tcTGag",
            this.resolveForCanonical(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "gAGGcc/gcc",
            this.resolveForCanonical(variantMockData.get("22:g.36689419_36689421del"))
        );

        assertEquals(
            "-/CAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT",
            this.codonChangeResolver.resolve(
                variantMockData.get("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT").getTranscriptConsequences().get(0)
            )
        );
    }

    @Test
    public void resolveCodonChange() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertNull(
            this.codonChangeResolver.resolve(
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertNull(
            this.codonChangeResolver.resolve(
                variantMockData.get("11:g.62393546_62393547delinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertNull(
            this.codonChangeResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(1)
            )
        );
    }

    private String resolveForCanonical(VariantAnnotation variantAnnotation)
    {
        return this.codonChangeResolver.resolve(
            this.canonicalTranscriptResolver.resolve(variantAnnotation)
        );
    }
}
