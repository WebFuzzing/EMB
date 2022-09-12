package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Rcv
{

    @Field(value = "accession")
    private String accession;

    @Field(value = "clinical_significance")
    private String clinicalSignificance;

    @Field(value = "origin")
    private String origin;    
    
    @Field(value = "preferred_name")
    private String preferredName;

    public String getAccession()
    {
        return accession;
    }

    public void setAccession(String accession)
    {
        this.accession = accession;
    }

    public String getClinicalSignificance()
    {
        return clinicalSignificance;
    }

    public void setClinicalSignificance(String clinicalSignificance)
    {
        this.clinicalSignificance = clinicalSignificance;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setOrigin(String origin)
    {
        this.origin = origin;
    }

    public String getPreferredName()
    {
        return preferredName;
    }

    public void setPreferredName(String preferredName)
    {
        this.preferredName = preferredName;
    }
}