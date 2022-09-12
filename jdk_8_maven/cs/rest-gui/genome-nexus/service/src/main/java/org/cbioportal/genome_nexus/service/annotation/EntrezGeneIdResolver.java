package org.cbioportal.genome_nexus.service.annotation;

import java.util.*;
import org.cbioportal.genome_nexus.component.annotation.CanonicalTranscriptResolver;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EntrezGeneIdResolver
{
    private final EnsemblService ensemblService;

    public EntrezGeneIdResolver(EnsemblService ensemblService)
    {
        this.ensemblService = ensemblService;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence) throws EnsemblWebServiceException
    {
        String entrezGeneId = null;

        if (transcriptConsequence != null &&
            transcriptConsequence.getGeneSymbol() != null &&
            !transcriptConsequence.getGeneSymbol().trim().isEmpty())
        {
            // TODO may add it back after having a map in database
            // EnsemblGene ensemblGene = null;

            // try {
            //     ensemblGene = this.ensemblService
            //             .getCanonicalEnsemblGeneIdByHugoSymbol(transcriptConsequence.getGeneSymbol());
            // } catch (NoEnsemblGeneIdForHugoSymbolException e) {
            //     // ignore silently
            // }

            // if (ensemblGene != null) {
            //     entrezGeneId = ensemblGene.getEntrezGeneId();
            // }
            // NOTE: Transcript consequence does not have an entrez gene id field, therefore can
            // only search for the entrez gene id by the hugo symbol.
            // TODO: allow searching in gene aliases and/or by entrez gene id to get the hugo symbol
            entrezGeneId = this.ensemblService.getEntrezGeneIdByHugoSymbol(transcriptConsequence.getGeneSymbol());
        }

        return entrezGeneId;
    }
}
