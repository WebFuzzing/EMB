package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HomozygotesMixin
{
    @JsonProperty(value = "hom", required = true)
    private Integer homozygotes;

    @JsonProperty(value = "hom_afr", required = true)
    private Integer homozygotesAfrican;

    @JsonProperty(value = "hom_amr", required = true)
    private Integer homozygotesLatino;

    @JsonProperty(value = "hom_asj", required = true)
    private Integer homozygotesAshkenaziJewish;

    @JsonProperty(value = "hom_eas", required = true)
    private Integer homozygotesEastAsian;

    @JsonProperty(value = "hom_fin", required = true)
    private Integer homozygotesEuropeanFinnish;

    @JsonProperty(value = "hom_nfe", required = true)
    private Integer homozygotesEuropeanNonFinnish;

    @JsonProperty(value = "hom_oth", required = true)
    private Integer homozygotesOther;

    @JsonProperty(value = "hom_sas", required = true)
    private Integer homozygotesSouthAsian;

}