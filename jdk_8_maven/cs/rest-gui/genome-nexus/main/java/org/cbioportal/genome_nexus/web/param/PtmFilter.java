package org.cbioportal.genome_nexus.web.param;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class PtmFilter
{
    @ApiModelProperty(value = EnsemblFilter.TRANSCRIPT_ID_DESC)
    private List<String> transcriptIds;

    public List<String> getTranscriptIds() {
        return transcriptIds;
    }

    public void setTranscriptIds(List<String> transcriptIds) {
        this.transcriptIds = transcriptIds;
    }
}
