package org.cbioportal.genome_nexus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "mutation_assessor.annotation")
public class MutationAssessor
{
    @Id
    private String input;

    private String hgvs;

    private String hugoSymbol;

    private String referenceGenomeVariant;
    private String referenceGenomeVariantType;

    private String functionalImpact;
    private Double functionalImpactScore;

    private String msaLink;
    private String pdbLink;

    private Double variantConservationScore;
    private Double variantSpecificityScore;

    private String mappingIssue;
    private String variant;
    private String uniprotId;
    private String refseqId;

    private Double msaGaps;
    private Integer msaHeight;

    private String codonStartPosition;
    private Integer uniprotPosition;
    private String uniprotResidue;
    private Integer refseqPosition;
    private String refseqResidue;

    private Integer cosmicCount;
    private Integer snpCount;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getHgvs() {
        return hgvs;
    }

    public void setHgvs(String hgvs) {
        this.hgvs = hgvs;
    }

    @Field("gene")
    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    @Field("rgaa")
    public String getReferenceGenomeVariant() {
        return referenceGenomeVariant;
    }

    public void setReferenceGenomeVariant(String referenceGenomeVariant) {
        this.referenceGenomeVariant = referenceGenomeVariant;
    }

    @Field("rgvt")
    public String getReferenceGenomeVariantType() {
        return referenceGenomeVariantType;
    }

    public void setReferenceGenomeVariantType(String referenceGenomeVariantType) {
        this.referenceGenomeVariantType = referenceGenomeVariantType;
    }

    @Field("F_impact")
    public String getFunctionalImpact() {
        return functionalImpact;
    }

    public void setFunctionalImpact(String functionalImpact) {
        this.functionalImpact = functionalImpact;
    }

    @Field("F_score")
    public Double getFunctionalImpactScore() {
        return functionalImpactScore;
    }

    public void setFunctionalImpactScore(Double functionalImpactScore) {
        this.functionalImpactScore = functionalImpactScore;
    }

    @Field("msa")
    public String getMsaLink() {
        return msaLink;
    }

    public void setMsaLink(String msaLink) {
        this.msaLink = msaLink;
    }

    @Field("pdb")
    public String getPdbLink() {
        return pdbLink;
    }

    public void setPdbLink(String pdbLink) {
        this.pdbLink = pdbLink;
    }

    @Field("vc_score")
    public Double getVariantConservationScore() {
        return variantConservationScore;
    }

    public void setVariantConservationScore(Double variantConservationScore) {
        this.variantConservationScore = variantConservationScore;
    }

    @Field("vs_score")
    public Double getVariantSpecificityScore() {
        return variantSpecificityScore;
    }

    public void setVariantSpecificityScore(Double variantSpecificityScore) {
        this.variantSpecificityScore = variantSpecificityScore;
    }

    @Field("info")
    public String getMappingIssue() {
        return mappingIssue;
    }

    public void setMappingIssue(String mappingIssue) {
        this.mappingIssue = mappingIssue;
    }

    @Field("var")
    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    @Field("uprot")
    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    @Field("rsprot")
    public String getRefseqId() {
        return refseqId;
    }

    public void setRefseqId(String refseqId) {
        this.refseqId = refseqId;
    }

    @Field("gaps")
    public Double getMsaGaps() {
        return msaGaps;
    }

    public void setMsaGaps(Double msaGaps) {
        this.msaGaps = msaGaps;
    }

    @Field("msa_height")
    public Integer getMsaHeight() {
        return msaHeight;
    }

    public void setMsaHeight(Integer msaHeight) {
        this.msaHeight = msaHeight;
    }

    @Field("chr")
    public String getCodonStartPosition() {
        return codonStartPosition;
    }

    public void setCodonStartPosition(String codonStartPosition) {
        this.codonStartPosition = codonStartPosition;
    }

    @Field("up_pos")
    public Integer getUniprotPosition() {
        return uniprotPosition;
    }

    public void setUniprotPosition(Integer uniprotPosition) {
        this.uniprotPosition = uniprotPosition;
    }

    @Field("up_res")
    public String getUniprotResidue() {
        return uniprotResidue;
    }

    public void setUniprotResidue(String uniprotResidue) {
        this.uniprotResidue = uniprotResidue;
    }

    @Field("rs_pos")
    public Integer getRefseqPosition() {
        return refseqPosition;
    }

    public void setRefseqPosition(Integer refseqPosition) {
        this.refseqPosition = refseqPosition;
    }

    @Field("rs_res")
    public String getRefseqResidue() {
        return refseqResidue;
    }

    public void setRefseqResidue(String refseqResidue) {
        this.refseqResidue = refseqResidue;
    }

    @Field("cnt_cosmic")
    public Integer getCosmicCount() {
        return cosmicCount;
    }

    public void setCosmicCount(Integer cosmicCount) {
        this.cosmicCount = cosmicCount;
    }

    @Field("cnt_snps")
    public Integer getSnpCount() {
        return snpCount;
    }

    public void setSnpCount(Integer snpCount) {
        this.snpCount = snpCount;
    }
}
