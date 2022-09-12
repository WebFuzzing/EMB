package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutationAssessorMixin
{
    @ApiModelProperty(value = "User-input variants", required = true)
    private String input;

    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    private String hugoSymbol;

    @ApiModelProperty(value = "Reference genome variant", required = false)
    private String referenceGenomeVariant;

    @ApiModelProperty(value = "Reference genome variant type", required = false)
    private String referenceGenomeVariantType;

    @ApiModelProperty(value = "Functional impact", required = false)
    private String functionalImpact;

    @ApiModelProperty(value = "Functional impact score", required = false)
    private double functionalImpactScore;

    @ApiModelProperty(value = "Link to multiple sequence alignment", required = false)
    private String msaLink;

    @ApiModelProperty(value = "Link to 3d structure browser", required = false)
    private String pdbLink;

    @ApiModelProperty(value = "Variant conservation score", required = false)
    private double variantConservationScore;

    @ApiModelProperty(value = "Variant specificity score", required = false)
    private double variantSpecificityScore;

    @ApiModelProperty(value = "Mapping issue info", required = false)
    private String mappingIssue;

    @ApiModelProperty(value = "Amino acid substitution", required = false)
    private String variant;

    @ApiModelProperty(value = "Uniprot protein accession ID", required = false)
    private String uniprotId;

    @ApiModelProperty(value = "Refseq protein ID", required = false)
    private String refseqId;

    @ApiModelProperty(value = "Portion of gaps in variant position in multiple sequence alignment", required = false)
    private double msaGaps;

    @ApiModelProperty(value = "Number of diverse sequences in multiple sequence alignment (identical or highly similar sequences filtered out)", required = false)
    private int msaHeight;

    @ApiModelProperty(value = "Codon start position", required = false)
    private String codonStartPosition;

    @ApiModelProperty(value = "Variant position in Uniprot protein, can be different from the one in Refseq", required = false)
    private int uniprotPosition;

    @ApiModelProperty(value = "Reference residue in Uniprot protein, can be different from the one in Refseq", required = false)
    private String uniprotResidue;

    @ApiModelProperty(value = "Variant position in Refseq protein, can be different from the one in Uniprot", required = false)
    private int refseqPosition;

    @ApiModelProperty(value = "Reference residue in Refseq protein, can be different from the one in Uniprot", required = false)
    private String refseqResidue;

    @ApiModelProperty(value = "Number of mutations in COSMIC for this protein", required = false)
    private int cosmicCount;

    @ApiModelProperty(value = "Number of SNPs in dbSNP for this protein", required = false)
    private int snpCount;
}
