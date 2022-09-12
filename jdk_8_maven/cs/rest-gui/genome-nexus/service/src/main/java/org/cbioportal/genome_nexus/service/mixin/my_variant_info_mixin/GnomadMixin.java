package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.AlleleCount;
import org.cbioportal.genome_nexus.model.my_variant_info_model.AlleleFrequency;
import org.cbioportal.genome_nexus.model.my_variant_info_model.AlleleNumber;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Homozygotes;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GnomadMixin
{
    @JsonProperty(value = "ac", required = true)
    private AlleleCount alleleCount;

    @JsonProperty(value = "an", required = true)
    private AlleleNumber alleleNumber;

    @JsonProperty(value = "hom", required = true)
    private Homozygotes homozygotes;

    @JsonProperty(value = "af", required = true)
    private AlleleFrequency alleleFrequency;

}