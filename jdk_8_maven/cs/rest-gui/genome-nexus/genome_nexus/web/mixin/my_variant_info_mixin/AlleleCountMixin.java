package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleCountMixin
{

    @ApiModelProperty(value = "ac", required = false)
    private Integer alleleCount;

    @ApiModelProperty(value = "ac_afr", required = false)
    private Integer alleleCountAfrican;

    @ApiModelProperty(value = "ac_amr", required = false)
    private Integer alleleCountLatino;

    @ApiModelProperty(value = "ac_asj", required = false)
    private Integer alleleCountAshkenaziJewish;

    @ApiModelProperty(value = "ac_eas", required = false)
    private Integer alleleCountEastAsian;

    @ApiModelProperty(value = "ac_fin", required = false)
    private Integer alleleCountEuropeanFinnish;

    @ApiModelProperty(value = "ac_nfe", required = false)
    private Integer alleleCountEuropeanNonFinnish;

    @ApiModelProperty(value = "ac_oth", required = false)
    private Integer alleleCountOther;

    @ApiModelProperty(value = "ac_sas", required = false)
    private Integer alleleCountSouthAsian;
}