package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.component.annotation.VariantTypeResolver;
import org.mockito.Mockito;

import java.util.Map;

public class VariantTypeResolverMocker
{
    public void mockMethods(Map<String, VariantAnnotation> variantMockData,
                            VariantTypeResolver variantTypeResolver)
    {
        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("3:g.14106026_14106037del"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("3:g.114058003del"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("6:g.137519505_137519506del"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("6:g.137519505_137519506delinsA"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        ).thenReturn(
            "SNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("9:g.135797242delinsAT"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("10:g.101953779del"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("11:g.62393546_62393547delinsAA"))
        ).thenReturn(
            "DNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        ).thenReturn(
            "SNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("13:g.28608258_28608275del"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("19:g.46141892_46141893delinsAA"))
        ).thenReturn(
            "DNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("22:g.29091840_29091841delinsCA"))
        ).thenReturn(
            "DNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("22:g.36689419_36689421del"))
        ).thenReturn(
            "DEL"
        );
    }
}
