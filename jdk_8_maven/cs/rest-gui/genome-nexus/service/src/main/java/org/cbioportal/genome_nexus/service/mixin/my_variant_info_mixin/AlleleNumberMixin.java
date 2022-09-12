package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleNumberMixin
{
    @JsonProperty(value = "an", required = true)
    private Integer alleleNumber;

    @JsonProperty(value = "an_afr", required = true)
    private Integer alleleNumberAfrican;

    @JsonProperty(value = "an_amr", required = true)
    private Integer alleleNumberLatino;

    @JsonProperty(value = "an_asj", required = true)
    private Integer alleleNumberAshkenaziJewish;

    @JsonProperty(value = "an_eas", required = true)
    private Integer alleleNumberEastAsian;

    @JsonProperty(value = "an_fin", required = true)
    private Integer alleleNumberEuropeanFinnish;

    @JsonProperty(value = "an_nfe", required = true)
    private Integer alleleNumberEuropeanNonFinnish;

    @JsonProperty(value = "an_oth", required = true)
    private Integer alleleNumberOther;

    @JsonProperty(value = "an_sas", required = true)
    private Integer alleleNumberSouthAsian;

}