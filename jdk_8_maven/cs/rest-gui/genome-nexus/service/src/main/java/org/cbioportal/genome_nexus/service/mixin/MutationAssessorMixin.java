package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MutationAssessorMixin
{
    @JsonProperty("gene")
    private String hugoSymbol;

    @JsonProperty("rgaa")
    private String referenceGenomeVariant;

    @JsonProperty("rgvt")
    private String referenceGenomeVariantType;

    @JsonProperty("F_impact")
    private String functionalImpact;

    @JsonProperty("F_score")
    private Double functionalImpactScore;

    @JsonProperty("msa")
    private String msaLink;

    @JsonProperty("pdb")
    private String pdbLink;

    @JsonProperty("vc_score")
    private Double variantConservationScore;

    @JsonProperty("vs_score")
    private Double variantSpecificityScore;

    @JsonProperty("info")
    private String mappingIssue;

    @JsonProperty("var")
    private String variant;

    @JsonProperty("uprot")
    private String uniprotId;

    @JsonProperty("rsprot")
    private String refseqId;

    @JsonProperty("gaps")
    private Double msaGaps;

    @JsonProperty("msa_height")
    private Integer msaHeight;

    @JsonProperty("chr")
    private String codonStartPosition;

    @JsonProperty("up_pos")
    private Integer uniprotPosition;

    @JsonProperty("up_res")
    private String uniprotResidue;

    @JsonProperty("rs_pos")
    private Integer refseqPosition;

    @JsonProperty("rs_res")
    private String refseqResidue;

    @JsonProperty("cnt_cosmic")
    private Integer cosmicCount;

    @JsonProperty("cnt_snps")
    private Integer snpCount;
}
