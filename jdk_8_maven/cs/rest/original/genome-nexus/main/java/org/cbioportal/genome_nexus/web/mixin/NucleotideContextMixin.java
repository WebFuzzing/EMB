package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NucleotideContextMixin
{
    @ApiModelProperty(value = "Nucleotide context sequence", required = true)
    private String seq;
}