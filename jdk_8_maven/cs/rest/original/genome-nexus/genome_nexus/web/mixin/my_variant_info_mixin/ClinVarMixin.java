package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Gene;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg38;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hgvs;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Rcv;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClinVarMixin
{
    @ApiModelProperty(value = "license", required = false)
    private String license;

    @ApiModelProperty(value = "allele_id", required = false)
    private Integer alleleId;

    @ApiModelProperty(value = "alt", required = false)
    private String alt;

    @ApiModelProperty(value = "chrom", required = false)
    private String chrom;

    @ApiModelProperty(value = "cytogenic", required = false)
    private String cytogenic;

    @ApiModelProperty(value = "gene", required = false)
    private Gene gene;

    @ApiModelProperty(value = "hg19", required = false)
    private Hg19 hg19;

    @ApiModelProperty(value = "hg38", required = false)
    private Hg38 hg38;

    @ApiModelProperty(value = "hgvs", required = false)
    private Hgvs hgvs;

    @ApiModelProperty(value = "rcv", required = false)
    private List<Rcv> tcv;

    @ApiModelProperty(value = "variant_id", required = false)
    private Integer variantId;
}