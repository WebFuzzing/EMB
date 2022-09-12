package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColocatedVariantMixin 
{
    @ApiModelProperty(value = "GnomAD Non-Finnish European Allele", position=1, required = true)
    private String gnomad_nfe_allele;

    @ApiModelProperty(value = "GnomAD Non-Finnish European MAF", position=2, required = true)
    private String gnomad_nfe_maf;

    @ApiModelProperty(value = "GnomAD African/African American Allele", position=3, required = true)
    private String gnomad_afr_allele;

    @ApiModelProperty(value = "GnomAD African/African American MAF", position=4, required = true)
    private String gnomad_afr_maf;

    @ApiModelProperty(value = "GnomAD East Asian Allele", position=5, required = true)
    private String gnomad_eas_allele;

    @ApiModelProperty(value = "GnomAD East Asian MAF", position=6, required = true)
    private String gnomad_eas_maf;

}