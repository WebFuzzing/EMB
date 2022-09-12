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
public class HugoGeneSymbolResolverTest
{
    @InjectMocks
    private HugoGeneSymbolResolver hugoGeneSymbolResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveHugoGeneSymbolForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "JAK1",
            this.resolveForCanonical(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "TPRXL",
            this.resolveForCanonical(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "FGD5",
            this.resolveForCanonical(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "ZBTB20",
            this.resolveForCanonical(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "DRD5",
            this.resolveForCanonical(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "SHROOM3",
            this.resolveForCanonical(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "IFNGR1",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "IFNGR1",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "BRAF",
            this.resolveForCanonical(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "GPR124",
            this.resolveForCanonical(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "TSC1",
            this.resolveForCanonical(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "CHUK",
            this.resolveForCanonical(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "GANAB",
            this.resolveForCanonical(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "KRAS",
            this.resolveForCanonical(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "FLT3",
            this.resolveForCanonical(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "USP7",
            this.resolveForCanonical(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "EML2",
            this.resolveForCanonical(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "CHEK2",
            this.resolveForCanonical(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "MYH9",
            this.resolveForCanonical(variantMockData.get("22:g.36689419_36689421del"))
        );
    }

    @Test
    public void resolveHugoGeneSymbol() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertEquals(
            "BRF2",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "B3GAT3",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData.get("11:g.62393546_62393547delinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "MIR330",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(1)
            )
        );
    }

    private String resolveForCanonical(VariantAnnotation variantAnnotation)
    {
        return this.hugoGeneSymbolResolver.resolve(
            this.canonicalTranscriptResolver.resolve(variantAnnotation)
        );
    }
}
