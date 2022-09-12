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

@RunWith(MockitoJUnitRunner.Silent.class)
public class TranscriptIdResolverTest
{
    @InjectMocks
    private TranscriptIdResolver transcriptIdResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveTranscriptIdForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "ENST00000342505",
            this.resolveForCanonical(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "ENST00000424053",
            this.resolveForCanonical(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "ENST00000285046",
            this.resolveForCanonical(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "ENST00000474710",
            this.resolveForCanonical(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "ENST00000304374",
            this.resolveForCanonical(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "ENST00000296043",
            this.resolveForCanonical(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "ENST00000367739",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "ENST00000367739",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "ENST00000288602",
            this.resolveForCanonical(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "ENST00000412232",
            this.resolveForCanonical(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "ENST00000298552",
            this.resolveForCanonical(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "ENST00000370397",
            this.resolveForCanonical(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "ENST00000346178",
            this.resolveForCanonical(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "ENST00000256078",
            this.resolveForCanonical(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "ENST00000241453",
            this.resolveForCanonical(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "ENST00000344836",
            this.resolveForCanonical(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "ENST00000587152",
            this.resolveForCanonical(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "ENST00000382580",
            this.resolveForCanonical(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "ENST00000216181",
            this.resolveForCanonical(variantMockData.get("22:g.36689419_36689421del"))
        );
    }

    private String resolveForCanonical(VariantAnnotation variantAnnotation)
    {
        return this.transcriptIdResolver.resolve(
            this.canonicalTranscriptResolver.resolve(variantAnnotation)
        );
    }
}
