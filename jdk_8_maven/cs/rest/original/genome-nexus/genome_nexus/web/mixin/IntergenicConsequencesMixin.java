package org.cbioportal.genome_nexus.web.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntergenicConsequencesMixin {
    @ApiModelProperty(value = "impact", position=1, required = true)
    private String impact;

    @ApiModelProperty(value = "variant_allele", position=2, required = true)
    private String variantAllele;

    @ApiModelProperty(value = "consequence_terms", position=3, required = true)
    private List<String> consequenceTerms;
}
