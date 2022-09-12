package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.SignalMutation;

import java.util.List;

public interface SignalMutationService
{
    List<SignalMutation> getSignalMutations();
    List<SignalMutation> getSignalMutations(String hugoGeneSymbol);
    List<SignalMutation> getSignalMutations(List<String> hugoGeneSymbols);
    List<SignalMutation> getSignalMutations(GenomicLocation genomicLocation);
    List<SignalMutation> getSignalMutationsByGenomicLocation(String genomicLocation);
    List<SignalMutation> getSignalMutationsByHgvsg(String hgvsg);
}
