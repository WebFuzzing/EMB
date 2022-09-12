package org.cbioportal.genome_nexus.web.mock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HotspotMixin
{
    @JsonProperty(value="hugo_symbol")
    private String hugoSymbol;
    
    @JsonProperty(value="transcript_id")
    private String transcriptId;

    @JsonProperty(value="residue")
    private String residue;

    @JsonProperty(value="tumor_count")
    private Integer tumorCount;

    @JsonProperty(value="type")
    private String type;

    @JsonProperty(value="missense_count")
    private Integer missenseCount;

    @JsonProperty(value="trunc_count")
    private Integer truncatingCount;

    @JsonProperty(value="inframe_count")
    private Integer inframeCount;

    @JsonProperty(value="splice_count")
    private Integer spliceCount;
}
