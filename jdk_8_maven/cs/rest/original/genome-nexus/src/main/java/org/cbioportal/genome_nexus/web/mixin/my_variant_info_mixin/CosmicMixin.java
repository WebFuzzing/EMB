package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;

import io.swagger.annotations.ApiModelProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CosmicMixin
{
    @ApiModelProperty(value = "_license", required = false)
    private String license;

    @ApiModelProperty(value = "alt", required = false)
    private String alt;

    @ApiModelProperty(value = "chrom", required = false)
    private String chrom;

    @ApiModelProperty(value = "cosmic_id", required = false)
    private String cosmicId;

    @ApiModelProperty(value = "hg19", required = false)
    private Hg19 hg19;

    @ApiModelProperty(value = "mut_freq", required = false)
    private Double mutFreq;

    @ApiModelProperty(value = "mut_nt", required = false)
    private String mutNt;

    @ApiModelProperty(value = "ref", required = false)
    private String ref;

    @ApiModelProperty(value = "tumor_site", required = false)
    private String tumorSite;
}