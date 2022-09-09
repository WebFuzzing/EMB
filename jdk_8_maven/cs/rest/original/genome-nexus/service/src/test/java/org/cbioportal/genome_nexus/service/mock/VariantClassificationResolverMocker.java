package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.component.annotation.VariantClassificationResolver;
import org.mockito.Mockito;

import java.util.Map;

public class VariantClassificationResolverMocker
{
    public void mockMethods(Map<String, VariantAnnotation> variantMockData,
                            VariantClassificationResolver variantClassificationResolver)
    {
        Mockito.when(
            variantClassificationResolver.resolve(null, variantMockData.get("7:g.55220240G>T").getTranscriptConsequences().get(0))
        ).thenReturn(
            "Splice_Region"
        );
    }
}
