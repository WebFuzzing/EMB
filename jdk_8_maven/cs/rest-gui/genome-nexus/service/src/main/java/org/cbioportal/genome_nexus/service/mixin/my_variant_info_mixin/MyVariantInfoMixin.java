package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.ClinVar;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Cosmic;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Gnomad;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Mutdb;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Snpeff;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Vcf;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MyVariantInfoMixin
{
    @JsonProperty(value = "_id", required = true)
    private String variant;

    @JsonProperty(value = "query", required = true)
    private String query;

    @JsonProperty(value = "version", required = true)
    private Integer version;

    @JsonProperty(value = "snpeff", required = true)
    private Snpeff snpeff;

    @JsonProperty(value = "vcf", required = true)
    private Vcf vcf;

    @JsonProperty(value = "dbsnp", required = true)
    private Integer dbsnp;

    @JsonProperty(value = "cosmic", required = true)
    private Cosmic cosmic;

    @JsonProperty(value = "clinvar", required = true)
    private ClinVar clinVar;

    @JsonProperty(value = "mutdb", required = true)
    private Mutdb mutdb;

    @JsonProperty(value = "gnomad_exome", required = true)
    private Gnomad gnomadExome;

    @JsonProperty(value = "gnomad_genome", required = true)
    private Gnomad gnomadGenome;
}
