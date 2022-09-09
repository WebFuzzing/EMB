package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotspotMixin
{
    @JsonIgnore
    private String id;

    @ApiModelProperty(value = "Hugo gene symbol")
    private String hugoSymbol;

    @ApiModelProperty(value = "Ensembl Transcript Id")
    private String transcriptId;

    @ApiModelProperty(value = "Hotspot residue")
    private String residue;

    @ApiModelProperty(value = "Hotspot type")
    private String type;

    @ApiModelProperty(value = "Tumor count")
    private Integer tumorCount;

    @ApiModelProperty(value="Missense mutation count")
    private Integer missenseCount;

    @ApiModelProperty(value="Truncation mutation count")
    private Integer truncatingCount;

    @ApiModelProperty(value="Inframe mutation count")
    private Integer inframeCount;

    @ApiModelProperty(value="Splice mutation count")
    private Integer spliceCount;
}
