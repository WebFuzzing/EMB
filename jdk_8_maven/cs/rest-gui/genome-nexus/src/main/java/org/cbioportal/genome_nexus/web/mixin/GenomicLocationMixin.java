package org.cbioportal.genome_nexus.web.mixin;

import io.swagger.annotations.ApiModelProperty;

public class GenomicLocationMixin
{
    @ApiModelProperty(value = "Chromosome", position = 1, required = true)
    private String chromosome;

    @ApiModelProperty(value = "Start Position", position = 2, required = true)
    private Integer start;

    @ApiModelProperty(value = "End Position", position = 3, required = true)
    private Integer end;

    @ApiModelProperty(value = "Reference Allele", position = 4, required = true)
    private String referenceAllele;

    @ApiModelProperty(value = "Variant Allele", position = 5, required = true)
    private String variantAllele;
}
