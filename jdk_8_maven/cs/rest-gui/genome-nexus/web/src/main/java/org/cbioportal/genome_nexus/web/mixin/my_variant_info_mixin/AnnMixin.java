package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnMixin
{
    @ApiModelProperty(value = "distance_to_feature", required = false)
    private String distanceToFeature;

    @ApiModelProperty(value = "effect", required = false)
    private String effect;

    @ApiModelProperty(value = "feature_id", required = false)
    private String featureId;

    @ApiModelProperty(value = "feature_type", required = false)
    private String featureType;

    @ApiModelProperty(value = "gene_id", required = false)
    private String geneId;

    @ApiModelProperty(value = "gene_name", required = false)
    private String geneName;

    @ApiModelProperty(value = "hgvs_c", required = false)
    private String hgvsCoding;

    @ApiModelProperty(value = "putative_impact", required = false)
    private String putativeImpact;

    @ApiModelProperty(value = "transcript_biotype", required = false)
    private String transcriptBiotype;
}
