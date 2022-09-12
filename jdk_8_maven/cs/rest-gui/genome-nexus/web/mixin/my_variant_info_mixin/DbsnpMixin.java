package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Alleles;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbsnpMixin
{
    @ApiModelProperty(value = "_license", required = false)
    private String license;

    @ApiModelProperty(value = "allele_origin", required = false)
    private String alleleOrigin;

    @ApiModelProperty(value = "alleles", required = false)
    private Alleles alleles;

    @ApiModelProperty(value = "alt", required = false)
    private String alt;

    @ApiModelProperty(value = "chrom", required = false)
    private String chrom;

    @ApiModelProperty(value = "class", required = false)
    private String _class;

    @ApiModelProperty(value = "dbsnp_build", required = false)
    private Integer dbsnpBuild;

    @ApiModelProperty(value = "flags", required = false)
    private String flags;

    @ApiModelProperty(value = "hg19", required = false)
    private Hg19 hg19;

    @ApiModelProperty(value = "ref", required = false)
    private String ref;

    @ApiModelProperty(value = "rsid", required = false)
    private String rsid;

    @ApiModelProperty(value = "validated", required = false)
    private Boolean validated;

    @ApiModelProperty(value = "var_subtype", required = false)
    private String varSubtype;

    @ApiModelProperty(value = "vartype", required = false)
    private String vartype;
}