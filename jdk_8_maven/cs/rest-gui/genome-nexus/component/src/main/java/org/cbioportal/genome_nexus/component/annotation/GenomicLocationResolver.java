package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GenomicLocationResolver
{
    @NotNull
    public GenomicLocation resolve(VariantAnnotation variantAnnotation)
    {
        GenomicLocation genomicLocation = new GenomicLocation();

        genomicLocation.setChromosome(this.resolveChromosome(variantAnnotation));
        genomicLocation.setStart(this.resolveStart(variantAnnotation));
        genomicLocation.setEnd(this.resolveEnd(variantAnnotation));
        genomicLocation.setReferenceAllele(this.resolveReferenceAllele(variantAnnotation));
        genomicLocation.setVariantAllele(this.resolveVariantAllele(variantAnnotation));

        return genomicLocation;
    }

    @Nullable
    private String resolveChromosome(VariantAnnotation variantAnnotation)
    {
        String chromosome = null;

        if (variantAnnotation != null &&
            variantAnnotation.getSeqRegionName() != null)
        {
            chromosome = variantAnnotation.getSeqRegionName();
        }

        return chromosome;
    }

    @Nullable
    public Integer resolveStart(VariantAnnotation variantAnnotation)
    {
        Integer start = variantAnnotation.getStart();
        Integer end = variantAnnotation.getEnd();

        if (start == null) {
            start = end;
        }

        // in case end < start, use end instead of start
        // (this indicates that start & end points are not properly reported though)
        if (end != null) {
            start = Math.min(start, end);
        }

        return start;
    }

    @Nullable
    public Integer resolveEnd(VariantAnnotation variantAnnotation)
    {
        Integer start = variantAnnotation.getStart();
        Integer end = variantAnnotation.getEnd();

        if (end == null) {
            end = start;
        }

        // in case end < start, use start instead of end
        // (this indicates that start & end points are not properly reported though)
        if (start != null) {
            end = Math.max(start, end);
        }

        return end;
    }

    @Nullable
    public String resolveReferenceAllele(VariantAnnotation variantAnnotation)
    {
        String referenceAllele = null;
        String[] alleles = extractAlleles(variantAnnotation);

        if (alleles != null && alleles.length == 2)
        {
            referenceAllele = alleles[0];
        }

        return referenceAllele;
    }

    @Nullable
    public String resolveVariantAllele(VariantAnnotation variantAnnotation)
    {
        String variantAllele = null;
        String[] alleles = extractAlleles(variantAnnotation);

        if (alleles != null && alleles.length == 2)
        {
            variantAllele = alleles[1];
        }

        return variantAllele;
    }

    @Nullable
    private String[] extractAlleles(VariantAnnotation variantAnnotation)
    {
        String[] alleles = null;

        if (variantAnnotation != null &&
            variantAnnotation.getAlleleString() != null)
        {
            alleles = variantAnnotation.getAlleleString().split("/", -1);
        }

        return alleles;
    }
}
