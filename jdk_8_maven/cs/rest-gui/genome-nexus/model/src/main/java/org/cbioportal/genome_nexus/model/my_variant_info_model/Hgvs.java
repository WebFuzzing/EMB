package org.cbioportal.genome_nexus.model.my_variant_info_model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Hgvs
{

    @Field(value = "coding")
    private List<String> coding;

    @Field(value = "genomic")
    private List<String> genomic;

    @Field(value = "protein")
    private List<String> protein;

    public List<String> getCoding()
    {
        return coding;
    }

    public void setCoding(List<String> coding)
    {
        this.coding = coding;
    }
    public List<String> getGenomic()
    {
        return genomic;
    }

    public void setGenomic(List<String> genomic)
    {
        this.genomic = genomic;
    }

    public List<String> getProtein()
    {
        return protein;
    }

    public void setProtein(List<String> protein)
    {
        this.protein = protein;
    }

}