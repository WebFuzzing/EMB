package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RcvMixin
{
    @JsonProperty(value = "accession", required = true)
    private String accession;

    @JsonProperty(value = "clinical_significance", required = true)
    private String clinicalSignificance;

    @JsonProperty(value = "origin", required = true)
    private String origin;

    @JsonProperty(value = "preferred_name", required = true)
    private String preferredName;

}