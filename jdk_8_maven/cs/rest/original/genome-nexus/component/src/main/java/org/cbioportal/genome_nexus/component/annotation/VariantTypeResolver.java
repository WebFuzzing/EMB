package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VariantTypeResolver
{
    private final GenomicLocationResolver genomicLocationResolver;

    @Autowired
    public VariantTypeResolver(GenomicLocationResolver genomicLocationResolver)
    {
        this.genomicLocationResolver = genomicLocationResolver;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        String variantType = null;

        String referenceAllele = this.genomicLocationResolver.resolveReferenceAllele(variantAnnotation);
        String variantAllele = this.genomicLocationResolver.resolveVariantAllele(variantAnnotation);

        if (referenceAllele != null &&
            variantAllele != null)
        {
            variantType = resolveVariantType(referenceAllele, variantAllele);
        }

        return variantType;
    }

    @Nullable
    private String resolveVariantType(String referenceAllele, String variantAllele)
    {
        String npType[] = {"SNP", "DNP", "TNP"};

        Integer refLength = referenceAllele == null || referenceAllele.equals("-") ? 0 : referenceAllele.length();
        Integer varLength = variantAllele == null || variantAllele.equals("-") ? 0 : variantAllele.length();

        if (refLength.equals(varLength))
        {
            if (refLength - 1 < 0) {
                // TODO log.info("Check " + mRecord.getTUMOR_SAMPLE_BARCODE() + " " + mRecord.getHUGO_SYMBOL());??
                return null;
            }

            return refLength <= 3 ? npType[refLength - 1] : "ONP";
        }
        else {
            return refLength < varLength ? "INS" : "DEL";
        }
    }
}
