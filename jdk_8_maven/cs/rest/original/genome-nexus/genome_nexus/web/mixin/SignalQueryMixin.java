package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.SignalMatchType;
import org.cbioportal.genome_nexus.model.SignalQueryType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalQueryMixin {
    @ApiModelProperty("Query Type")
    private SignalQueryType queryType;

    @ApiModelProperty("Match Type")
    private SignalMatchType matchType;

    @ApiModelProperty("Matching Hugo Gene Symbol (e.g: BRCA2, BRAF)")
    private String hugoSymbol;

    @ApiModelProperty("Matching Alteration (e.g: V600E)")
    private String alteration;

    @ApiModelProperty("Matching Region (e.g: 13:32968940-32968940)")
    private String region;

    @ApiModelProperty("Matching HGVSG Variant (e.g: 17:g.37880220T>C)")
    private String variant;

    @ApiModelProperty("Optional free-form text")
    private String description;
}
