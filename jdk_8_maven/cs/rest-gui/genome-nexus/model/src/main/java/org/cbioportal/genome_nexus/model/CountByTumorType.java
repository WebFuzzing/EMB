package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class CountByTumorType
{
    @Field("tumor_type")
    private String tumorType;

    @Field("tumor_type_count")
    private Integer tumorTypeCount;

    @Field("variant_count")
    private Integer variantCount;

    public String getTumorType() {
        return tumorType;
    }

    public void setTumorType(String tumorType) {
        this.tumorType = tumorType;
    }

    public Integer getTumorTypeCount() {
        return tumorTypeCount;
    }

    public void setTumorTypeCount(Integer tumorTypeCount) {
        this.tumorTypeCount = tumorTypeCount;
    }

    public Integer getVariantCount() {
        return variantCount;
    }

    public void setVariantCount(Integer variantCount) {
        this.variantCount = variantCount;
    }
}
