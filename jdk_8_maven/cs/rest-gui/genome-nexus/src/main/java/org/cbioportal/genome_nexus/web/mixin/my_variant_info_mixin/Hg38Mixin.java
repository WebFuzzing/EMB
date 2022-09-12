package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hg38Mixin
{
    @ApiModelProperty(value = "start", required = false)
    private String start;

    @ApiModelProperty(value = "end", required = false)
    private String end;
}