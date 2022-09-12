package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.IntegerRange;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptConsequenceSummaryMixin
{
    @JsonProperty(required = true)
    @ApiModelProperty(value = "Transcript id", required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Codon change")
    private String codonChange;

    @ApiModelProperty(value = "Amino acids change")
    private String aminoAcids;

    @ApiModelProperty(value = "Reference Amino Acid")
    private String aminoAcidRef;

    @ApiModelProperty(value = "Alt Amino Acid")
    private String aminoAcidAlt;

    @ApiModelProperty(value = "Entrez gene id")
    private String entrezGeneId;

    @ApiModelProperty(value = "Consequence terms (comma separated)")
    private String consequenceTerms;

    @ApiModelProperty(value = "Hugo gene symbol")
    private String hugoGeneSymbol;

    @ApiModelProperty(value = "HGVSp short")
    private String hgvspShort;

    @ApiModelProperty(value = "HGVSp")
    private String hgvsp;

    @ApiModelProperty(value = "HGVSc")
    private String hgvsc;

    @ApiModelProperty(value = "Protein position (start and end)")
    private IntegerRange proteinPosition;

    @ApiModelProperty(value = "RefSeq id")
    private String refSeq;

    @ApiModelProperty(value = "Variant classification")
    private String variantClassification;

    @ApiModelProperty(value = "Polyphen Score")
    private Double polyphenScore;

    @ApiModelProperty(value = "Polyphen Prediction")
    private String polyphenPrediction;

    @ApiModelProperty(value = "Sift Score")
    private Double siftScore;

    @ApiModelProperty(value = "Sift Prediction")
    private String siftPrediction;

    @ApiModelProperty(value = "Uniprot ID")
    private String uniprotId;
}
