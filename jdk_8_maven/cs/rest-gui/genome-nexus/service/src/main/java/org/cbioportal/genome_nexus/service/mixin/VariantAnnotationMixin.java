package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.*;

import org.cbioportal.genome_nexus.model.IntergenicConsequences;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VariantAnnotationMixin
{
    @JsonProperty(value="input", required = true)
    private String variant;

    @JsonProperty(value="original_variant_query", required = true)
    private String originalVariantQuery;

    @JsonProperty(required = true)
    private String annotationJSON;

    @JsonProperty(value="id", required = true)
    private String variantId;

    @JsonProperty(value="intergenic_consequences", required = true)
    private List<IntergenicConsequences> intergenicConsequences;

    @JsonProperty(value="assembly_name", required = true)
    private String assemblyName;

    @JsonProperty(value="seq_region_name", required = true)
    private String seqRegionName;

    @JsonProperty(value="start", required = true)
    public Integer getStart()
    {
        return start;
    }
    private Integer start;

    @JsonProperty(value="end", required = true)
    private Integer end;

    @JsonProperty(value="allele_string", required = true)
    private String alleleString;

    @JsonProperty(value="strand", required = true)
    private Integer strand;

    @JsonProperty(value="most_severe_consequence", required = true)
    private String mostSevereConsequence;

    @JsonProperty(value="transcript_consequences", required = true)
    private List<TranscriptConsequence> transcriptConsequences;

    @JsonIgnore
    private Map<String, Object> dynamicProps;
}
