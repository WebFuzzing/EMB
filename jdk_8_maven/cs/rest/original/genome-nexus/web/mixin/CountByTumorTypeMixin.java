package org.cbioportal.genome_nexus.web.mixin;

import io.swagger.annotations.ApiModelProperty;

public class CountByTumorTypeMixin
{
    @ApiModelProperty("Tumor Type")
    private String tumorType;

    @ApiModelProperty("Sample count for Tumor Type")
    private Integer tumorTypeCount;

    @ApiModelProperty("Variant count for Tumor Type")
    private Integer variantCount;
}
