package org.cbioportal.genome_nexus.service.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.cbioportal.genome_nexus.model.*;

import org.cbioportal.genome_nexus.model.my_variant_info_model.*;

import org.cbioportal.genome_nexus.service.mixin.*;

import org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin.*;

import org.springframework.stereotype.Component;

@Component("defaultExternalResourceObjectMapper")
public class ExternalResourceObjectMapper extends ObjectMapper
{
    public ExternalResourceObjectMapper()
    {
        // convert any single value to an array if we define our model as a list
        // this is useful for external services which return a single value when there is
        // a single result, and a list if there are multiple results
        // TODO causes problems when mapping back from mongo collections, disabling for now
        // this.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();

        mixinMap.put(GeneXref.class, GeneXrefMixin.class);
        mixinMap.put(MutationAssessor.class, MutationAssessorMixin.class);
        mixinMap.put(NucleotideContext.class, NucleotideContextMixin.class);
        mixinMap.put(TranscriptConsequence.class, TranscriptConsequenceMixin.class);
        mixinMap.put(VariantAnnotation.class, VariantAnnotationMixin.class);
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
        super.setMixIns(mixinMap);
    }
}
