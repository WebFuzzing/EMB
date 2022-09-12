package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VcfMixin
{
    @JsonProperty(value = "alt", required = true)
    private String alt;

    @JsonProperty(value = "position", required = true)
    private String position;

    @JsonProperty(value = "ref", required = true)
    private String ref;
}
