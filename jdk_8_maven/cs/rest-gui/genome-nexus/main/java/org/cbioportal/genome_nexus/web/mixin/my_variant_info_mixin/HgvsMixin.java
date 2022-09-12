package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class HgvsMixin
{
    @ApiModelProperty(value = "coding", required = false)
    private List<String> coding;

    @ApiModelProperty(value = "genomic", required = false)
    private List<String> genomic;

    @ApiModelProperty(value = "protein", required = false)
    private List<String> protein;
}