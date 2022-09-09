package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PfamDomainRangeMixin {
    @ApiModelProperty(value = "Pfam domain id", position=1, required = true)
    private String pfamDomainId;

    @ApiModelProperty(value = "Pfam domain start amino acid", position=2, required = true)
    private String pfamDomainStart;

    @ApiModelProperty(value = "Pfam domain end amino acid", position=3, required = true)
    private String pfamDomainEnd;
}
