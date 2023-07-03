package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleNumberMixin
{

    @ApiModelProperty(value = "an", required = false)
    private Integer alleleNumber;

    @ApiModelProperty(value = "an_afr", required = false)
    private Integer alleleNumberAfrican;

    @ApiModelProperty(value = "an_amr", required = false)
    private Integer alleleNumberLatino;

    @ApiModelProperty(value = "an_asj", required = false)
    private Integer alleleNumberAshkenaziJewish;

    @ApiModelProperty(value = "an_eas", required = false)
    private Integer alleleNumberEastAsian;

    @ApiModelProperty(value = "an_fin", required = false)
    private Integer alleleNumberEuropeanFinnish;

    @ApiModelProperty(value = "an_nfe", required = false)
    private Integer alleleNumberEuropeanNonFinnish;

    @ApiModelProperty(value = "an_oth", required = false)
    private Integer alleleNumberOther;

    @ApiModelProperty(value = "an_sas", required = false)
    private Integer alleleNumberSouthAsian;
}