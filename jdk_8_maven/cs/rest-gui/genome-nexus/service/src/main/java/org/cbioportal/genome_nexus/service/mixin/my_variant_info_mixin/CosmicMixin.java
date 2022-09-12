package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CosmicMixin
{
    @JsonProperty(value = "_license", required = true)
    private String license;

    @JsonProperty(value = "alt", required = true)
    private String alt;

    @JsonProperty(value = "chrom", required = true)
    private String chrom;

    @JsonProperty(value = "cosmic_id", required = true)
    private String cosmicId;

    @JsonProperty(value = "hg19", required = true)
    private Hg19 hg19;

    @JsonProperty(value = "mut_freq", required = true)
    private Double mutFreq;

    @JsonProperty(value = "mut_nt", required = true)
    private String mutNt;

    @JsonProperty(value = "ref", required = true)
    private String ref;

    @JsonProperty(value = "tumor_site", required = true)
    private String tumorSite;
}