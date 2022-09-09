package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExonMixin {
    @ApiModelProperty(value = "Exon id", position=1, required = true)
    private String exonId;

    @ApiModelProperty(value = "Start position of exon", position=2, required = true)
    private String exonStart;

    @ApiModelProperty(value = "End position of exon", position=3, required = true)
    private String exonEnd;

    @ApiModelProperty(value = "Number of exon in transcript", position=4, required = true)
    private String rank;

    @ApiModelProperty(value = "Strand exon is on, -1 for - and 1 for +", position=5, required = true)
    private String strand;

    @ApiModelProperty(value = "Exon version", position=6, required = true)
    private String version;
}
