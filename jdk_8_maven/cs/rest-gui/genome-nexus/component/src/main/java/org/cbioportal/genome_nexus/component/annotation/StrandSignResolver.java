package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class StrandSignResolver
{
    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        String sign = null;
        Integer strand = null;

        if (variantAnnotation != null) {
            strand = variantAnnotation.getStrand();
        }

        if (strand != null)
        {
            if (strand < 0) {
                sign = "-";
            }
            else {
                sign = "+";
            }
        }

        return sign;
    }
}
