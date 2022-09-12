package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.my_variant_info_model.AlleleCount;
import org.cbioportal.genome_nexus.model.my_variant_info_model.AlleleFrequency;
import org.cbioportal.genome_nexus.model.my_variant_info_model.AlleleNumber;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Homozygotes;

import io.swagger.annotations.ApiModelProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GnomadMixin
{

    @ApiModelProperty(value = "ac", required = false)
    private AlleleCount alleleCount;

    @ApiModelProperty(value = "an", required = false)
    private AlleleNumber alleleNumber;

    @ApiModelProperty(value = "hom", required = false)
    private Homozygotes homozygotes;

    @ApiModelProperty(value = "af", required = false)
    private AlleleFrequency alleleFrequency;

}