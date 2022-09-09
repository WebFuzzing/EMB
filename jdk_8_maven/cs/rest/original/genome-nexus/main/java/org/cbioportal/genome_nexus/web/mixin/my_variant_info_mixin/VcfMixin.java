package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VcfMixin
{
    @ApiModelProperty(value = "alt", required = false)
    private String alt;

    @ApiModelProperty(value = "position", required = false)
    private String position;

    @ApiModelProperty(value = "ref", required = false)
    private String ref;
}
