package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptConsequenceMixin
{
    @JsonProperty(value="transcript_id", required = true)
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @JsonProperty(value="hgvsp", required = true)
    @ApiModelProperty(value = "HGVSp", required = false)
    private String hgvsp;

    @JsonProperty(value="hgvsc", required = true)
    @ApiModelProperty(value = "HGVSc", required = false)
    private String hgvsc;

    @JsonProperty(value="hgvsg", required = true)
    @ApiModelProperty(value = "HGVSg", required = false)
    private String hgvsg;

    @JsonProperty(value="variant_allele", required = true)
    @ApiModelProperty(value = "Variant allele", required = false)
    private String variantAllele;

    @JsonProperty(value="codons", required = true)
    @ApiModelProperty(value = "Codons", required = false)
    private String codons;

    @JsonProperty(value="protein_id", required = true)
    @ApiModelProperty(value = "Ensembl protein id", required = false)
    private String proteinId;

    @JsonProperty(value="protein_start", required = true)
    @ApiModelProperty(value = "Protein start position", required = false)
    private Integer proteinStart;

    @JsonProperty(value="protein_end", required = true)
    @ApiModelProperty(value = "Protein end position", required = false)
    private Integer proteinEnd;

    @JsonProperty(value="gene_symbol", required = true)
    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    private String geneSymbol;

    @JsonProperty(value="gene_id", required = true)
    @ApiModelProperty(value = "Ensembl gene id", required = false)
    private String geneId;

    @JsonProperty(value="amino_acids", required = true)
    @ApiModelProperty(value = "Amino acids", required = false)
    private String aminoAcids;

    @JsonProperty(value="hgnc_id", required = true)
    @ApiModelProperty(value = "HGNC id", required = false)
    private String hgncId;

    @JsonProperty(value="canonical", required = true)
    @ApiModelProperty(value = "Canonical transcript indicator", required = false)
    private String canonical;

    @JsonProperty(value="polyphen_score", required = true)
    @ApiModelProperty(value = "Polyphen Score", required = false)
    private Double polyphenScore;

    @JsonProperty(value="polyphen_prediction", required = true)
    @ApiModelProperty(value = "Polyphen Prediction", required = false)
    private String polyphenPrediction;

    @JsonProperty(value="sift_score", required = true)
    @ApiModelProperty(value = "Sift Score", required = false)
    private Double siftScore;

    @JsonProperty(value="sift_prediction", required = true)
    @ApiModelProperty(value = "Sift Prediction", required = false)
    private String siftPrediction;

    @JsonProperty(value="refseq_transcript_ids", required = true)
    @ApiModelProperty(value = "List of RefSeq transcript ids", required = false)
    private List<String> refseqTranscriptIds;

    @JsonProperty(value="consequence_terms", required = true)
    @ApiModelProperty(value = "List of consequence terms", required = false)
    private List<String> consequenceTerms;

    @JsonIgnore
    private Map<String, Object> dynamicProps;

    @JsonAnySetter
    public void setDynamicProp(String key, Object value) {

    }

    @JsonAnyGetter
    public Map<String, Object> getDynamicProps() {
        return null;
    }
}
