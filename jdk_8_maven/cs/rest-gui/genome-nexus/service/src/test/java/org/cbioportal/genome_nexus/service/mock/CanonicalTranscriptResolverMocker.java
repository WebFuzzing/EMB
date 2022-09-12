package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.component.annotation.CanonicalTranscriptResolver;
import org.mockito.Mockito;

import java.util.Map;

public class CanonicalTranscriptResolverMocker
{
    public void mockMethods(
        Map<String, VariantAnnotation> variantMockData,
        CanonicalTranscriptResolver canonicalTranscriptResolver
    ) {
        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        ).thenReturn(
            variantMockData.get("1:g.65325832_65325833insG").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("3:g.14106026_14106037del"))
        ).thenReturn(
            variantMockData.get("3:g.14106026_14106037del").getTranscriptConsequences().get(1)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        ).thenReturn(
            variantMockData.get("3:g.14940279_14940280insCAT").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("3:g.114058003del"))
        ).thenReturn(
            variantMockData.get("3:g.114058003del").getTranscriptConsequences().get(5)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        ).thenReturn(
            variantMockData.get("4:g.9784947_9784948insAGA").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        ).thenReturn(
            variantMockData.get("4:g.77675978_77675979insC").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("6:g.137519505_137519506del"))
        ).thenReturn(
            variantMockData.get("6:g.137519505_137519506del").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("6:g.137519505_137519506delinsA"))
        ).thenReturn(
            variantMockData.get("6:g.137519505_137519506delinsA").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        ).thenReturn(
            variantMockData.get("7:g.140453136A>T").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        ).thenReturn(
            variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(2)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("9:g.135797242delinsAT"))
        ).thenReturn(
            variantMockData.get("9:g.135797242delinsAT").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("10:g.101953779del"))
        ).thenReturn(
            variantMockData.get("10:g.101953779del").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("11:g.62393546_62393547delinsAA"))
        ).thenReturn(
            variantMockData.get("11:g.62393546_62393547delinsAA").getTranscriptConsequences().get(1)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        ).thenReturn(
            variantMockData.get("12:g.25398285C>A").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("13:g.28608258_28608275del"))
        ).thenReturn(
            variantMockData.get("13:g.28608258_28608275del").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        ).thenReturn(
            variantMockData.get("16:g.9057113_9057114insCTG").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("19:g.46141892_46141893delinsAA"))
        ).thenReturn(
            variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(8)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("22:g.29091840_29091841delinsCA"))
        ).thenReturn(
            variantMockData.get("22:g.29091840_29091841delinsCA").getTranscriptConsequences().get(5)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("22:g.36689419_36689421del"))
        ).thenReturn(
            variantMockData.get("22:g.36689419_36689421del").getTranscriptConsequences().get(0)
        );

        Mockito.when(
            canonicalTranscriptResolver.resolve(variantMockData.get("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT"))
        ).thenReturn(
            variantMockData.get("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT").getTranscriptConsequences().get(0)
        );
    }
}
