package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EntrezGeneXrefResolver
{
    @Nullable
    public GeneXref resolve(List<GeneXref> geneXrefs, String geneSymbol)
    {
        GeneXref entrezGeneXref = null;

        // filter xrefs by gene symbol first
        geneXrefs = geneXrefs.stream().filter(xref ->
            xref.getDbName().equals("EntrezGene") &&
                xref.getDisplayId().equals(geneSymbol)
        ).collect(Collectors.toList());

        // if more than one xrefs, then further filter by description
        if (geneXrefs.size() > 1)
        {
            Optional<GeneXref> first = geneXrefs.stream().filter(xref ->
                !xref.getDescription().toLowerCase().contains("pseudogene") &&
                    !xref.getDescription().toLowerCase().contains("uncharacterized")
            ).findFirst();

            if (first.isPresent()) {
                entrezGeneXref = first.get();
            }
        }
        else if (geneXrefs.size() == 1) {
            entrezGeneXref = geneXrefs.get(0);
        }

        return entrezGeneXref;
    }
}
