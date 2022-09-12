package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.component.annotation.GenomicLocationResolver;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.SignalAnnotation;
import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.SignalMutationService;

import java.util.List;

public class SignalAnnotationEnricher extends BaseAnnotationEnricher
{
    private final SignalMutationService signalMutationService;
    private final GenomicLocationResolver genomicLocationResolver;

    public SignalAnnotationEnricher(
        String id,
        SignalMutationService signalMutationService
    ) {
        super(id);
        this.signalMutationService = signalMutationService;
        this.genomicLocationResolver = new GenomicLocationResolver();
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        SignalAnnotation signalAnnotation = new SignalAnnotation();
        GenomicLocation genomicLocation = this.genomicLocationResolver.resolve(annotation);
        List<SignalMutation> mutations = this.signalMutationService.getSignalMutations(genomicLocation);
        signalAnnotation.setAnnotation(mutations);
        annotation.setSignalAnnotation(signalAnnotation);
    }
}
