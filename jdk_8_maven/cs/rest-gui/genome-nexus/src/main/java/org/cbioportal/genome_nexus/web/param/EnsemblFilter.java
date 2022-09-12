package org.cbioportal.genome_nexus.web.param;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class EnsemblFilter
{
    public static final String TRANSCRIPT_ID_DESC =
        "List of Ensembl transcript IDs. For example [\"ENST00000361390\", \"ENST00000361453\", \"ENST00000361624\"]";

    public static final String PROTEIN_ID_DESC =
        "List of Ensembl protein IDs. For example [\"ENSP00000439985\", \"ENSP00000478460\", \"ENSP00000346196\"]";

    public static final String GENE_ID_DESC =
        "List of Ensembl gene IDs. For example [\"ENSG00000136999\", \"ENSG00000272398\", \"ENSG00000198695\"]";

    public static final String HUGO_SYMBOL_DESC =
        "List of Hugo Symbols. For example [\"TP53\", \"PIK3CA\", \"BRCA1\"]";

    @ApiModelProperty(value = TRANSCRIPT_ID_DESC)
    private List<String> transcriptIds;

    @ApiModelProperty(value = PROTEIN_ID_DESC)
    private List<String> proteinIds;

    @ApiModelProperty(value = GENE_ID_DESC)
    private List<String> geneIds;

    @ApiModelProperty(value = HUGO_SYMBOL_DESC)
    private List<String> hugoSymbols;

    public List<String> getTranscriptIds() {
        return transcriptIds;
    }

    public void setTranscriptIds(List<String> transcriptIds) {
        this.transcriptIds = transcriptIds;
    }

    public List<String> getProteinIds() {
        return proteinIds;
    }

    public void setProteinIds(List<String> proteinIds) {
        this.proteinIds = proteinIds;
    }

    public List<String> getGeneIds() {
        return geneIds;
    }

    public void setGeneIds(List<String> geneIds) {
        this.geneIds = geneIds;
    }

    public List<String> getHugoSymbols() {
        return hugoSymbols;
    }

    public void setHugoSymbols(List<String> hugoSymbols) {
        this.hugoSymbols = hugoSymbols;
    }
}
