package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.CanonicalTranscriptResolverMocker;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.cbioportal.genome_nexus.service.mock.VariantTypeResolverMocker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
public class VariantClassificationResolverTest
{
    @InjectMocks
    private VariantClassificationResolver variantClassificationResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Mock
    private VariantTypeResolver variantTypeResolver;

    @Spy
    private TranscriptConsequencePrioritizer consequencePrioritizer;

    @Spy
    private GenomicLocationResolver genomicLocationResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();
    private final VariantTypeResolverMocker variantTypeResolverMocker = new VariantTypeResolverMocker();

    @Test
    public void resolveVariantClassificationForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);
        this.variantTypeResolverMocker.mockMethods(variantMockData, this.variantTypeResolver);

        assertEquals(
            "Frame_Shift_Ins",
            this.resolveForCanonical(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "In_Frame_Del",
            this.resolveForCanonical(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "In_Frame_Ins",
            this.resolveForCanonical(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "Frame_Shift_Del",
            this.resolveForCanonical(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "In_Frame_Ins",
            this.resolveForCanonical(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "Frame_Shift_Ins",
            this.resolveForCanonical(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "Frame_Shift_Del",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "Frame_Shift_Del",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "Missense_Mutation",
            this.resolveForCanonical(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "Frame_Shift_Ins",
            this.resolveForCanonical(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "Frame_Shift_Ins",
            this.resolveForCanonical(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "Frame_Shift_Del",
            this.resolveForCanonical(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "Nonsense_Mutation",
            this.resolveForCanonical(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "Missense_Mutation",
            this.resolveForCanonical(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "In_Frame_Del",
            this.resolveForCanonical(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "In_Frame_Ins",
            this.resolveForCanonical(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "Splice_Site",
            this.resolveForCanonical(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "Missense_Mutation",
            this.resolveForCanonical(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "In_Frame_Del",
            this.resolveForCanonical(variantMockData.get("22:g.36689419_36689421del"))
        );
    }

    @Test
    public void resolveVariantClassification() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.variantTypeResolverMocker.mockMethods(variantMockData, this.variantTypeResolver);

        assertEquals(
            "3'Flank",
            this.variantClassificationResolver.resolve(
                variantMockData.get("8:g.37696499_37696500insG"),
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "5'Flank",
            this.variantClassificationResolver.resolve(
                variantMockData.get("11:g.62393546_62393547delinsAA"),
                variantMockData.get("11:g.62393546_62393547delinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "3'Flank",
            this.variantClassificationResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delinsAA"),
                variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(1)
            )
        );

        assertEquals(
            "Splice_Region",
            this.variantClassificationResolver.resolve(
                null,
                variantMockData.get("7:g.55220240G>T").getTranscriptConsequences().get(0)
            )
        );
    }

    private String resolveForCanonical(VariantAnnotation variantAnnotation)
    {
        return this.variantClassificationResolver.resolve(
            variantAnnotation,
            this.canonicalTranscriptResolver.resolve(variantAnnotation)
        );
    }
}
