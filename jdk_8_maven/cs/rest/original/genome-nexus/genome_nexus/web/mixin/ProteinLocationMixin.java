package org.cbioportal.genome_nexus.web.mixin;

import io.swagger.annotations.ApiModelProperty;

public class ProteinLocationMixin
{
    @ApiModelProperty(value = "Ensembl Transcript ID", position = 1, required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Start Position Residue", position = 2, required = true)
    private Integer start;
    
    @ApiModelProperty(value = "End Position Residue", position = 3, required = true)
    private Integer end;

    @ApiModelProperty(value = "Mutation Type e.g. Missense_Mutation", position = 4, required = true)
    private String mutationType;
}