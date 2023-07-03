package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleFrequencyMixin
{
    @JsonProperty(value = "af", required = true)
    private Double alleleFrequency;

    @JsonProperty(value = "af_afr", required = true)
    private Double alleleFrequencyAfrican;

    @JsonProperty(value = "af_amr", required = true)
    private Double alleleFrequencyLatino;

    @JsonProperty(value = "af_asj", required = true)
    private Double alleleFrequencyAshkenaziJewish;

    @JsonProperty(value = "af_eas", required = true)
    private Double alleleFrequencyEastAsian;

    @JsonProperty(value = "af_fin", required = true)
    private Double alleleFrequencyEuropeanFinnish;

    @JsonProperty(value = "af_nfe", required = true)
    private Double alleleFrequencyEuropeanNonFinnish;

    @JsonProperty(value = "af_oth", required = true)
    private Double alleleFrequencyOther;

    @JsonProperty(value = "af_sas", required = true)
    private Double alleleFrequencySouthAsian;

}