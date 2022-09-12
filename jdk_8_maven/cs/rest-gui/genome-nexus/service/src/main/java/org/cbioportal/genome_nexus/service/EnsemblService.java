package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.service.exception.EnsemblTranscriptNotFoundException;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForHugoSymbolException;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForEntrezGeneIdException;
import java.util.*;

public interface EnsemblService
{
    EnsemblTranscript getEnsemblTranscriptsByTranscriptId(String transcriptId)
        throws EnsemblTranscriptNotFoundException;

    EnsemblGene getCanonicalEnsemblGeneIdByHugoSymbol(String hugoSymbol)
        throws NoEnsemblGeneIdForHugoSymbolException;
    List<EnsemblGene> getCanonicalEnsemblGeneIdByHugoSymbols(List<String> hugoSymbol);

    EnsemblGene getCanonicalEnsemblGeneIdByEntrezGeneId(String entrezGeneId)
        throws NoEnsemblGeneIdForEntrezGeneIdException;
    List<EnsemblGene> getCanonicalEnsemblGeneIdByEntrezGeneIds(List<String> entrezGeneId);

    EnsemblTranscript getCanonicalEnsemblTranscriptByHugoSymbol(String hugoSymbol, String isoformOverrideSource)
        throws EnsemblTranscriptNotFoundException;
    List<EnsemblTranscript> getCanonicalEnsemblTranscriptsByHugoSymbols(List<String> hugoSymbol, String isoformOverrideSource);

    List<EnsemblTranscript> getEnsemblTranscripts(String geneId, String proteinId, String hugoSymbol);
    List<EnsemblTranscript> getEnsemblTranscripts(List<String> transcriptIds, List<String> geneIds, List<String> proteinIds, List<String> hugoSymbols);
    String getEntrezGeneIdByHugoSymbol(String hugoSymbol);
    List<String> getEntrezGeneIdByHugoSymbol(String hugoSymbol, Boolean searchInAliases);
    String getHugoSymbolByEntrezGeneId(String entrezGeneId);

    Set<String> getCanonicalTranscriptIdsBySource(String isoformOverrideSource);
    String getUniprotId(String transcript);
}
