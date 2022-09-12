package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnsemblGeneMixin {
    @ApiModelProperty(value = "Ensembl gene id", position=1, required = true)
    private String geneId;

    @ApiModelProperty(value = "Approved Hugo symbol", position=2, required = true)
    private String hugoSymbol;

    @ApiModelProperty(value = "Hugo symbol synonyms", position=3, required = false)
    private String[] synonyms;

    @ApiModelProperty(value = "Previous Hugo symbols", position=4, required = false)
    private String[] previousSymbols;

    @ApiModelProperty(value = "Entrez Gene Id", position=5, required = false)
    private String entrezGeneId;
}