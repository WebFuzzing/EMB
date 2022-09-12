package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RcvMixin
{
    @ApiModelProperty(value = "accession", required = false)
    private String accession;

    @ApiModelProperty(value = "clinical_significance", required = false)
    private String clinicalSignificance;

    @ApiModelProperty(value = "origin", required = false)
    private String origin;

    @ApiModelProperty(value = "preferred_name", required = false)
    private String preferredName;

}