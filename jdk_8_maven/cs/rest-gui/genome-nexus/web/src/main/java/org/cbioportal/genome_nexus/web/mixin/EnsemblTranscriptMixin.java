package org.cbioportal.genome_nexus.web.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnsemblTranscriptMixin {
    @ApiModelProperty(value = "Ensembl transcript id", position=1, required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Ensembl gene id", position=2, required = true)
    private String geneId;

    @ApiModelProperty(value = "Ensembl protein id", position=3, required = true)
    private String proteinId;

    @ApiModelProperty(value = "Length of protein", position=4)
    private Integer proteinLength;

    @ApiModelProperty(value = "Pfam domains", position=5, dataType="List")
    private List<PfamDomainRangeMixin> pfamDomains;

    @ApiModelProperty(value = "Hugo symbols", position=6, dataType="List")
    private List<String> hugoSymbols;

    @ApiModelProperty(value = "RefSeq mRNA ID", position=7)
    private String refseqMrnaId;

    @ApiModelProperty(value = "Consensus CDS (CCDS) ID", position=8)
    private String ccdsId;

    @ApiModelProperty(value = "Exon information", position=9, dataType="List")
    private List<ExonMixin> exons;

    @ApiModelProperty(value = "UTR information", position=10, dataType="List")
    private List<UntranslatedRegionMixin> utrs;
}
