package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfoAnnotation;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantAnnotationMixin {

    @JsonProperty(required = true)
    @ApiModelProperty(value = "Variant key", required = true)
    private String variant;

    @JsonProperty(required = true)
    @ApiModelProperty(value = "Original variant query", required = true)
    private String originalVariantQuery;

    @JsonProperty(required = false)
    @ApiModelProperty(value = "Annotation data as JSON string", required = false)
    private String annotationJSON;

    @JsonProperty(value="id", required = true)
    @ApiModelProperty(value = "Variant id", required = true)
    private String variantId;

    @JsonProperty(value="intergenic_consequences", required = true)
    @ApiModelProperty(value = "intergenicConsequences", required = true)
    private List<IntergenicConsequences> intergenicConsequences;

    @JsonProperty(value="assembly_name", required = true)
    @ApiModelProperty(value = "NCBI build number", required = false)
    private String assemblyName;

    @JsonProperty(value="seq_region_name", required = true)
    @ApiModelProperty(value = "Chromosome", required = false)
    private String seqRegionName;

    @JsonProperty(value="start", required = true)
    @ApiModelProperty(value = "Start position", required = false)
    public Integer getStart()
    {
        return start;
    }
    private Integer start;

    @JsonProperty(value="end", required = true)
    @ApiModelProperty(value = "End position", required = false)
    private Integer end;

    @JsonProperty(value="allele_string", required = true)
    @ApiModelProperty(value = "Allele string (e.g: A/T)", required = false)
    private String alleleString;

    @JsonProperty(value="strand", required = true)
    @ApiModelProperty(value = "Strand (negative or positive)", required = false)
    private Integer strand;

    @JsonProperty(value="most_severe_consequence", required = true)
    @ApiModelProperty(value = "Most severe consequence", required = false)
    private String mostSevereConsequence;

    @JsonProperty(value="transcript_consequences", required = true)
    @ApiModelProperty(value = "List of transcripts", required = false)
    private List<TranscriptConsequence> transcriptConsequences;

    @JsonProperty(value="successfully_annotated", required = true)
    @ApiModelProperty(value = "Status flag indicating whether variant was succesfully annotated", required = false)
    private Boolean successfullyAnnotated;

    @JsonProperty(value="mutation_assessor", required = true)
    @ApiModelProperty(value = "Mutation Assessor Annotation", required = false)
    private MutationAssessorAnnotation mutationAssessorAnnotation;

    @JsonProperty(value="nucleotide_context", required = true)
    @ApiModelProperty(value = "Nucleotide Context Annotation", required = false)
    private NucleotideContextAnnotation nucleotideContextAnnotation;

    @JsonProperty(value="hotspots", required = true)
    @ApiModelProperty(value = "Hotspot Annotation", required = false)
    private HotspotAnnotation hotspotAnnotation;

    @JsonProperty(value="ptms", required = true)
    @ApiModelProperty(value = "Post Translational Modifications", required = false)
    private PtmAnnotation ptmAnnotation;

    @JsonProperty(value="oncokb", required = true)
    @ApiModelProperty(value = "Oncokb", required = false)
    private OncokbAnnotation oncokbAnnotation;

    @JsonProperty(value="annotation_summary", required = true)
    @ApiModelProperty(value = "Variant Annotation Summary", required = false)
    private VariantAnnotationSummary annotationSummary;

    @JsonProperty(value="my_variant_info", required = true)
    @ApiModelProperty(value = "My Variant Info Annotation", required = false)
    private MyVariantInfoAnnotation myVariantInfoAnnotation;

    @JsonProperty(value="clinvar", required = true)
    @ApiModelProperty(value = "ClinVar", required = false)
    private ClinvarAnnotation clinvarAnnotation;

    @JsonIgnore
    private Map<String, Object> dynamicProps;

    @JsonAnySetter
    public void setDynamicProp(String key, Object value) {

    }

    @JsonAnyGetter
    public Map<String, Object> getDynamicProps() {
        return null;
    }
}
