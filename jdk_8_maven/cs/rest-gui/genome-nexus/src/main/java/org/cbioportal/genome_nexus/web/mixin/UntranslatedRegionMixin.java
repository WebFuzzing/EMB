package org.cbioportal.genome_nexus.web.mixin;

import io.swagger.annotations.ApiModelProperty;

public class UntranslatedRegionMixin {
    @ApiModelProperty(value = "UTR Type", position=1, required = true)
    private String type;

    @ApiModelProperty(value = "Start position of UTR", position=2, required = true)
    private String start;

    @ApiModelProperty(value = "End position of UTR", position=3, required = true)
    private String end;

    @ApiModelProperty(value = "Strand UTR is on, -1 for - and 1 for +", position=4, required = true)
    private String strand;
}
