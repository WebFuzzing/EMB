package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleFrequencyMixin
{

    @ApiModelProperty(value = "af", required = false)
    private Double alleleFrequency;

    @ApiModelProperty(value = "af_afr", required = false)
    private Double alleleFrequencyAfrican;

    @ApiModelProperty(value = "af_amr", required = false)
    private Double alleleFrequencyLatino;

    @ApiModelProperty(value = "af_asj", required = false)
    private Double alleleFrequencyAshkenaziJewish;

    @ApiModelProperty(value = "af_eas", required = false)
    private Double alleleFrequencyEastAsian;

    @ApiModelProperty(value = "af_fin", required = false)
    private Double alleleFrequencyEuropeanFinnish;

    @ApiModelProperty(value = "af_nfe", required = false)
    private Double alleleFrequencyEuropeanNonFinnish;

    @ApiModelProperty(value = "af_oth", required = false)
    private Double alleleFrequencyOther;

    @ApiModelProperty(value = "af_sas", required = false)
    private Double alleleFrequencySouthAsian;
}