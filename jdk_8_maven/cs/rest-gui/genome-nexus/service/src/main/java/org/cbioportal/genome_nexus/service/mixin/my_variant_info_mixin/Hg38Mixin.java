package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hg38Mixin
{
    @JsonProperty(value = "start", required = true)
    private String start;

    @JsonProperty(value = "end", required = true)
    private String end;
}