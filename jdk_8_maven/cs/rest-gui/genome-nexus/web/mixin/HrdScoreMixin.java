package org.cbioportal.genome_nexus.web.mixin;

import io.swagger.annotations.ApiModelProperty;

public class HrdScoreMixin {
    @ApiModelProperty("Median HRD LST")
    private Double lst;

    @ApiModelProperty("Median HRD ntelomeric AI")
    private Double ntelomericAi;

    @ApiModelProperty("Median HRD Fraction LOH")
    private Double fractionLoh;
}
