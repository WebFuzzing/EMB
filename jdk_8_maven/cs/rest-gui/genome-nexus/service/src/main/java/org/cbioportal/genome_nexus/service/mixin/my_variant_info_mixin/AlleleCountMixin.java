package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleCountMixin
{
    @JsonProperty(value = "ac", required = true)
    private Integer alleleCount;

    @JsonProperty(value = "ac_afr", required = true)
    private Integer alleleCountAfrican;

    @JsonProperty(value = "ac_amr", required = true)
    private Integer alleleCountLatino;

    @JsonProperty(value = "ac_asj", required = true)
    private Integer alleleCountAshkenaziJewish;

    @JsonProperty(value = "ac_eas", required = true)
    private Integer alleleCountEastAsian;

    @JsonProperty(value = "ac_fin", required = true)
    private Integer alleleCountEuropeanFinnish;

    @JsonProperty(value = "ac_nfe", required = true)
    private Integer alleleCountEuropeanNonFinnish;

    @JsonProperty(value = "ac_oth", required = true)
    private Integer alleleCountOther;

    @JsonProperty(value = "ac_sas", required = true)
    private Integer alleleCountSouthAsian;

}