package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.Hotspot;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregatedHotspotsMixin
{
    @ApiModelProperty(value = "Genomic Location", required = true)
    private GenomicLocation genomicLocation;

    @ApiModelProperty(value = "HGVS notation", required = true)
    private String variant;

    @ApiModelProperty(value = "Hotspots", required = true)
    private List<Hotspot> hotspots;
}
