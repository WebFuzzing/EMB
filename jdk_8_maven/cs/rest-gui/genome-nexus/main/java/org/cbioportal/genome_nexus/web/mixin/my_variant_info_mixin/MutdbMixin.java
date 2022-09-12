package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;

import io.swagger.annotations.ApiModelProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutdbMixin
{
    @ApiModelProperty(value = "rsid", required = false)
    private String rsid;

    @ApiModelProperty(value = "ref", required = false)
    private String ref;

    @ApiModelProperty(value = "alt", required = false)
    private String alt;

    @ApiModelProperty(value = "uniprot_id", required = false)
    private String uniprotId;

    @ApiModelProperty(value = "mutpred_score", required = false)
    private Double mutpredScore;

    @ApiModelProperty(value = "cosmic_id", required = false)
    private String cosmicId;

    @ApiModelProperty(value = "chrom", required = false)
    private String chrom;

    @ApiModelProperty(value = "hg19", required = false)
    private Hg19 hg19;
}