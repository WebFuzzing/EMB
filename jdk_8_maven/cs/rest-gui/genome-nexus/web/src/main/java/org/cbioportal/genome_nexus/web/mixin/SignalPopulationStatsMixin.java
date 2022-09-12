package org.cbioportal.genome_nexus.web.mixin;

import io.swagger.annotations.ApiModelProperty;

public class SignalPopulationStatsMixin {
    @ApiModelProperty("Impact")
    private Double impact;

    @ApiModelProperty("European")
    private Double eur;

    @ApiModelProperty("African/African American")
    private Double afr;

    @ApiModelProperty("Asian")
    private Double asn;

    @ApiModelProperty("Ashkenazi Jewish")
    private Double asj;

    @ApiModelProperty("Other")
    private Double oth;
}
