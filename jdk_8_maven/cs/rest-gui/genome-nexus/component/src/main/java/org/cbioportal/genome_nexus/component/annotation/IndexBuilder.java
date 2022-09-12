package org.cbioportal.genome_nexus.component.annotation;
import com.mongodb.lang.Nullable;
import org.cbioportal.genome_nexus.model.Index;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexBuilder {
    private final ProteinChangeResolver proteinChangeResolver;
    private final HugoGeneSymbolResolver hugoGeneSymbolResolver;
    
    @Autowired
    public IndexBuilder(ProteinChangeResolver proteinChangeResolver, HugoGeneSymbolResolver hugoGeneSymbolResolver)
    {
        this.proteinChangeResolver = proteinChangeResolver;
        this.hugoGeneSymbolResolver = hugoGeneSymbolResolver;
    }
    @NotNull
    public Index buildIndex(VariantAnnotation variantAnnotation)
    {
        Index index = new Index();
        index.setVariant(this.resolveVariant(variantAnnotation));
        index.setHugoSymbol(this.hugoGeneSymbolResolver.resolveAllHugoGeneSymbols(variantAnnotation));
        index.setHgvsp(this.proteinChangeResolver.resolveAllHgvsp(variantAnnotation));
        index.setHgvsc(this.proteinChangeResolver.resolveAllHgvsc(variantAnnotation));
        index.setCdna(this.proteinChangeResolver.resolveAllCdna(variantAnnotation));
        index.setHgvspShort(this.proteinChangeResolver.resolveAllHgvspShort(variantAnnotation));

        return index;
    }

    @Nullable
    private String resolveVariant(VariantAnnotation variantAnnotation)
    {
        String variant = null;

        if (variantAnnotation != null &&
            variantAnnotation.getHgvsg() != null)
        {
            variant = variantAnnotation.getHgvsg();
        }

        return variant;
    }


}
