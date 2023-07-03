package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="clinvar.mutation")
@CompoundIndex(def = "{'chromosome': 1, 'start_position': 1, 'end_position': 1}", name = "clinvar.mutation")
public class Clinvar {
    @Field(value = "chromosome")
    private String chromosome;

    @Field(value = "start_position")
    private Integer startPosition;

    @Field(value = "end_position")
    private Integer endPosition;

    @Field(value = "reference_allele")
    private String referenceAllele;

    @Field(value = "alternate_allele")
    private String alternateAllele;

    @Field(value = "clinvar_id")
    private Integer clinvarId;

    @Field(value = "clnsig")
    private String clinicalSignificance;

    @Field(value = "clnsigconf")
    private String conflictingClinicalSignificance;

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public String getAlternateAllele() {
        return alternateAllele;
    }

    public void setAlternateAllele(String alternateAllele) {
        this.alternateAllele = alternateAllele;
    }

    public Integer getClinvarId() {
        return clinvarId;
    }

    public void setClinvarId(Integer clinvarId) {
        this.clinvarId = clinvarId;
    }

    public String getClinicalSignificance() {
        return clinicalSignificance;
    }

    public void setClinicalSignificance(String clinicalSignificance) {
        this.clinicalSignificance = clinicalSignificance;
    }

    public String getConflictingClinicalSignificance() {
        return conflictingClinicalSignificance;
    }

    public void setConflictingClinicalSignificance(String conflictingClinicalSignificance) {
        this.conflictingClinicalSignificance = conflictingClinicalSignificance;
    }
}
