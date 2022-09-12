package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HgvsMixin
{
    @JsonProperty(value = "coding", required = true)
    private List<String> coding;

    @JsonProperty(value = "genomic", required = true)
    private List<String> genomic;

    @JsonProperty(value = "protein", required = false)
    private List<String> protein;
}