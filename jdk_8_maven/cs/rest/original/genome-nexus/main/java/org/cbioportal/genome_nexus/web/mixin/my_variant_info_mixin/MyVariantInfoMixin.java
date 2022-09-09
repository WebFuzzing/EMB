package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.my_variant_info_model.ClinVar;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Cosmic;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Dbsnp;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Gnomad;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Snpeff;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Vcf;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyVariantInfoMixin
{
    @ApiModelProperty(value = "variant", required = false)
    private String variant;

    @ApiModelProperty(value = "hgvs", required = false)
    private String hgvs;

    @ApiModelProperty(value = "version", required = false)
    private Integer version;

    @ApiModelProperty(value = "snpeff", required = false)
    private Snpeff snpeff;

    @ApiModelProperty(value = "vcf", required = false)
    private Vcf vcf;

    @ApiModelProperty(value = "dbsnp", required = false)
    private Dbsnp dbsnp;

    @ApiModelProperty(value = "cosmic", required = false)
    private Cosmic cosmic;

    @ApiModelProperty(value = "clinvar", required = false)
    private ClinVar clinVar;

    @ApiModelProperty(value = "gnomad_exome", required = false)
    private Gnomad gnomadExome;

    @ApiModelProperty(value = "gnomad_genome", required = false)
    private Gnomad gnomadGenome;
}
