package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.persistence.SignalMutationRepository;
import org.cbioportal.genome_nexus.service.SignalMutationService;
import org.cbioportal.genome_nexus.util.GenomicVariant;
import org.cbioportal.genome_nexus.util.GenomicVariantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SignalMutationServiceImpl implements SignalMutationService
{
    private final SignalMutationRepository signalMutationRepository;
    private final NotationConverter notationConverter;

    @Autowired
    public SignalMutationServiceImpl(SignalMutationRepository signalMutationRepository,
                                     NotationConverter notationConverter) {
        this.signalMutationRepository = signalMutationRepository;
        this.notationConverter = notationConverter;
    }

    @Override
    public List<SignalMutation> getSignalMutations() {
        return this.signalMutationRepository.findAll();
    }

    @Override
    public List<SignalMutation> getSignalMutations(String hugoGeneSymbol) {
        return this.signalMutationRepository.findByHugoGeneSymbol(hugoGeneSymbol);
    }

    @Override
    public List<SignalMutation> getSignalMutations(List<String> hugoGeneSymbols) {
        return this.signalMutationRepository.findByHugoGeneSymbolIn(hugoGeneSymbols);
    }

    @Override
    public List<SignalMutation> getSignalMutationsByGenomicLocation(String genomicLocation) {
        return this.getSignalMutations(this.notationConverter.parseGenomicLocation(genomicLocation));
    }

    @Override
    public List<SignalMutation> getSignalMutationsByHgvsg(String hgvsg) {
        GenomicLocation genomicLocation = this.notationConverter.hgvsgToGenomicLocation(hgvsg);
        GenomicVariant gv = GenomicVariantUtil.fromHgvs(hgvsg);

        if (genomicLocation == null) {
            return Collections.emptyList();
        }
        if (gv.getType() == GenomicVariant.Type.INDEL || gv.getType() == GenomicVariant.Type.DELETION) {
            return this.signalMutationRepository.findByChromosomeAndStartPositionAndEndPositionAndVariantAllele(	
                genomicLocation.getChromosome(),	
                genomicLocation.getStart().longValue(),	
                genomicLocation.getEnd().longValue(),	
                genomicLocation.getVariantAllele()	
            );
        } else {
            return this.getSignalMutations(genomicLocation);	
        }
    }

    @Override
    public List<SignalMutation> getSignalMutations(GenomicLocation genomicLocation) {
        if (genomicLocation == null) {
            return Collections.emptyList();
        }
        return this.signalMutationRepository.findByChromosomeAndStartPositionAndEndPositionAndReferenceAlleleAndVariantAllele(
            genomicLocation.getChromosome(),
            genomicLocation.getStart().longValue(),
            genomicLocation.getEnd().longValue(),
            genomicLocation.getReferenceAllele(),
            genomicLocation.getVariantAllele()
        );
    }
}
