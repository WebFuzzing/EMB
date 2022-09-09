package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.CountByTumorType;
import org.cbioportal.genome_nexus.model.GeneralPopulationStats;
import org.cbioportal.genome_nexus.model.StatsByTumorType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalMutationMixin
{
    @ApiModelProperty("Hugo Gene Symbol")
    private String hugoGeneSymbol;

    @ApiModelProperty("Chromosome")
    private String chromosome;

    @ApiModelProperty("Start Position")
    private Long startPosition;

    @ApiModelProperty("End Position")
    private Long endPosition;

    @ApiModelProperty("Reference Allele")
    private String referenceAllele;

    @ApiModelProperty("Variant Allele")
    private String variantAllele;

    @ApiModelProperty("Mutation Status")
    private String mutationStatus;

    @ApiModelProperty("Pathogenic")
    private String pathogenic;

    @ApiModelProperty("Penetrance")
    private String penetrance;

    @ApiModelProperty("Number Of Germline Homozygous Per Variant")
    private Integer overallNGermlineHomozygous;

    @ApiModelProperty("Counts by Tumor Type")
    private List<CountByTumorType> countsByTumorType;

    @ApiModelProperty("Biallelic Counts by Tumor Type")
    private List<CountByTumorType> biallelicCountsByTumorType;

    @ApiModelProperty("QC Pass Counts by Tumor Type")
    private List<CountByTumorType> qcPassCountsByTumorType;

    @ApiModelProperty("General Population Stats")
    private GeneralPopulationStats generalPopulationStats;

    @ApiModelProperty("Msk Expert Review")
    private Boolean mskExperReview;

    @ApiModelProperty("Stats By Tumor Type")
    private StatsByTumorType statsByTumorType;
}
