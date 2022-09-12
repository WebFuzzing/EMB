package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdbHeaderMixin
{
    @ApiModelProperty(value = "PDB id", required = true)
    private String pdbId;

    @ApiModelProperty(value = "PDB description", required = true)
    private String title;

    private Map<String, Object> compound;
    private Map<String, Object> source;
}
