package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Gnomad
{

    @Field(value = "ac")
    private AlleleCount alleleCount;

    @Field(value = "an")
    private AlleleNumber alleleNumber;

    @Field(value = "af")
    private AlleleFrequency alleleFrequency;

    @Field(value = "hom")
    private Homozygotes homozygotes;

    public AlleleCount getAlleleCount()
    {
        return alleleCount;
    }

    public void setAlleleCount(AlleleCount alleleCount)
    {
        this.alleleCount = alleleCount;
    }

    public AlleleNumber getAlleleNumber()
    {
        return alleleNumber;
    }

    public void setAlleleNumber(AlleleNumber alleleNumber)
    {
        this.alleleNumber = alleleNumber;
    }

    public AlleleFrequency getAlleleFrequency()
    {
        return alleleFrequency;
    }

    public void setAlleleFrequency(AlleleFrequency alleleFrequency)
    {
        this.alleleFrequency = alleleFrequency;
    }

    public Homozygotes getHomozygotes()
    {
        return homozygotes;
    }

    public void setHomozygotes(Homozygotes homozygotes)
    {
        this.homozygotes = homozygotes;
    }

}