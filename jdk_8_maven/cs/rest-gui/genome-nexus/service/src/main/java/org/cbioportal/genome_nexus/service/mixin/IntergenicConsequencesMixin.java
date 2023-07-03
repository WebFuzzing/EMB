package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntergenicConsequencesMixin
{
    @JsonProperty(value="impact", required = true)
    private String impact;

    @JsonProperty(value="variant_allele", required = true)
    private String variantAllele;

    @JsonProperty(value="consequence_terms", required = true)
    private List<String> consequenceTerms;

}
