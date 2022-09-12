package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MutdbMixin
{
    @JsonProperty(value = "rsid", required = true)
    private String rsid;

    @JsonProperty(value = "ref", required = true)
    private String ref;

    @JsonProperty(value = "alt", required = true)
    private String alt;

    @JsonProperty(value = "uniprot_id", required = true)
    private String uniprotId;

    @JsonProperty(value = "mutpred_score", required = true)
    private Double mutpredScore;

    @JsonProperty(value = "cosmic_id", required = true)
    private String cosmicId;

    @JsonProperty(value = "chrom", required = true)
    private String chrom;

    @JsonProperty(value = "hg19", required = true)
    private Hg19 hg19;
}