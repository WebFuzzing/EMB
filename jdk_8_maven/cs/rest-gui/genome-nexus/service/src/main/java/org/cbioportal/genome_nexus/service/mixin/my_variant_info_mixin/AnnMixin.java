package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnMixin
{
    @JsonProperty(value = "distance_to_feature", required = true)
    private String distanceToFeature;

    @JsonProperty(value = "effect", required = true)
    private String effect;

    @JsonProperty(value = "feature_id", required = true)
    private String featureId;

    @JsonProperty(value = "feature_type", required = true)
    private String featureType;

    @JsonProperty(value = "gene_id", required = true)
    private String geneId;

    @JsonProperty(value = "gene_name", required = true)
    private String geneName;

    @JsonProperty(value = "hgvs_c", required = true)
    private String hgvsCoding;

    @JsonProperty(value = "putative_impact", required = true)
    private String putativeImpact;

    @JsonProperty(value = "transcript_biotype", required = true)
    private String transcriptBiotype;
}
