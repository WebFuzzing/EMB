package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Alleles
{

    @Field(value = "allele")
    private String allele;


    public String getAllele()
    {
        return allele;
    }

    public void setAllele(String allele)
    {
        this.allele = allele;
    }

}