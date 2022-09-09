package org.cbioportal.genome_nexus.component.annotation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;


@Component
public class HugoGeneSymbolResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String hugoSymbol = null;

        if (transcriptConsequence != null &&
            transcriptConsequence.getGeneSymbol() != null &&
            !transcriptConsequence.getGeneSymbol().trim().isEmpty())
        {
            hugoSymbol = transcriptConsequence.getGeneSymbol();
        }

        return hugoSymbol;
    }

    @Nullable
    public List<String> resolveAllHugoGeneSymbols(VariantAnnotation variantAnnotation)
    {
        List<String> hugoSymbol = null;

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null &&
            variantAnnotation.getTranscriptConsequences().size() > 0)
        {
            Set<String> hugoSymbolSet = new HashSet<>();
            for (TranscriptConsequence transcriptConsequence : variantAnnotation.getTranscriptConsequences()) {
                if (transcriptConsequence.getGeneSymbol() != null) {
                    hugoSymbolSet.add(transcriptConsequence.getGeneSymbol());
                }
            }
            hugoSymbol = hugoSymbolSet.stream().collect(Collectors.toList());
        }

        return hugoSymbol;
    }
}
