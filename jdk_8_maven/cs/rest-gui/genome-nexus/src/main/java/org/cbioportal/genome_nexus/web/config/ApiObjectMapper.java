package org.cbioportal.genome_nexus.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.web.mixin.*;
import org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin.*;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.model.my_variant_info_model.*;
import org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin.AlleleCountMixin;
import org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin.AlleleFrequencyMixin;
import org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin.AlleleNumberMixin;
import org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin.HomozygotesMixin;

import java.util.HashMap;
import java.util.Map;


public class ApiObjectMapper extends ObjectMapper
{
    public ApiObjectMapper()
    {
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();

        mixinMap.put(GeneXref.class, GeneXrefMixin.class);
        mixinMap.put(Hotspot.class, HotspotMixin.class);
        mixinMap.put(MutationAssessor.class, MutationAssessorMixin.class);
        mixinMap.put(NucleotideContext.class, NucleotideContextMixin.class);
        mixinMap.put(PfamDomain.class, PfamDomainMixin.class);
        mixinMap.put(PdbHeader.class, PdbHeaderMixin.class);
        mixinMap.put(TranscriptConsequence.class, TranscriptConsequenceMixin.class);
        mixinMap.put(ColocatedVariant.class, ColocatedVariantMixin.class);
        mixinMap.put(TranscriptConsequenceSummary.class, TranscriptConsequenceSummaryMixin.class);
        mixinMap.put(VariantAnnotation.class, VariantAnnotationMixin.class);
        mixinMap.put(VariantAnnotationSummary.class, VariantAnnotationSummaryMixin.class);
        mixinMap.put(EnsemblTranscript.class, EnsemblTranscriptMixin.class);
        mixinMap.put(EnsemblGene.class, EnsemblGeneMixin.class);
        mixinMap.put(PfamDomainRange.class, PfamDomainRangeMixin.class);
        mixinMap.put(Exon.class, ExonMixin.class);
        mixinMap.put(UntranslatedRegion.class, UntranslatedRegionMixin.class);
        mixinMap.put(GenomicLocation.class, GenomicLocationMixin.class);
        mixinMap.put(AggregatedHotspots.class, AggregatedHotspotsMixin.class);
        mixinMap.put(ProteinLocation.class, ProteinLocationMixin.class);
        mixinMap.put(MyVariantInfo.class, MyVariantInfoMixin.class);
        mixinMap.put(Snpeff.class, SnpeffMixin.class);
        mixinMap.put(Ann.class, AnnMixin.class);
        mixinMap.put(Vcf.class, VcfMixin.class);
        mixinMap.put(Dbsnp.class, DbsnpMixin.class);
        mixinMap.put(Gene.class, GeneMixin.class);
        mixinMap.put(Alleles.class, AllelesMixin.class);
        mixinMap.put(Hg19.class, Hg19Mixin.class);
        mixinMap.put(Cosmic.class, CosmicMixin.class);
        mixinMap.put(ClinVar.class, ClinVarMixin.class);
        mixinMap.put(Hg38.class, Hg38Mixin.class);
        mixinMap.put(Mutdb.class, MutdbMixin.class);
        mixinMap.put(Gnomad.class, GnomadMixin.class);
        mixinMap.put(Rcv.class, RcvMixin.class);
        mixinMap.put(AlleleCount.class, AlleleCountMixin.class);
        mixinMap.put(AlleleNumber.class, AlleleNumberMixin.class);
        mixinMap.put(Homozygotes.class, HomozygotesMixin.class);
        mixinMap.put(AlleleFrequency.class, AlleleFrequencyMixin.class);
        mixinMap.put(IntergenicConsequences.class, IntergenicConsequencesMixin.class);
        mixinMap.put(SignalMutation.class, SignalMutationMixin.class);
        mixinMap.put(SignalQuery.class, SignalQueryMixin.class);
        mixinMap.put(CountByTumorType.class, CountByTumorTypeMixin.class);
        mixinMap.put(GeneralPopulationStats.class, GeneralPopulationStatsMixin.class);
        mixinMap.put(StatsByTumorType.class, StatsByTumorTypeMixin.class);
        mixinMap.put(SignalPopulationStats.class, SignalPopulationStatsMixin.class);
        mixinMap.put(HrdScore.class, HrdScoreMixin.class);
        super.setMixIns(mixinMap);
    }
}
