package org.cbioportal.genome_nexus.web.mixin;

import org.cbioportal.genome_nexus.model.SignalPopulationStats;

import io.swagger.annotations.ApiModelProperty;

public class GeneralPopulationStatsMixin {
    @ApiModelProperty("Counts")
    private SignalPopulationStats counts;

    @ApiModelProperty("Frequencies")
    private SignalPopulationStats frequencies;
}
