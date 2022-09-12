package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomozygotesMixin
{

    @ApiModelProperty(value = "hom", required = false)
    private Integer homozygotes;

    @ApiModelProperty(value = "hom_afr", required = false)
    private Integer homozygotesAfrican;

    @ApiModelProperty(value = "hom_amr", required = false)
    private Integer homozygotesLatino;

    @ApiModelProperty(value = "hom_asj", required = false)
    private Integer homozygotesAshkenaziJewish;

    @ApiModelProperty(value = "hom_eas", required = false)
    private Integer homozygotesEastAsian;

    @ApiModelProperty(value = "hom_fin", required = false)
    private Integer homozygotesEuropeanFinnish;

    @ApiModelProperty(value = "hom_nfe", required = false)
    private Integer homozygotesEuropeanNonFinnish;

    @ApiModelProperty(value = "hom_oth", required = false)
    private Integer homozygotesOther;

    @ApiModelProperty(value = "hom_sas", required = false)
    private Integer homozygotesSouthAsian;
}