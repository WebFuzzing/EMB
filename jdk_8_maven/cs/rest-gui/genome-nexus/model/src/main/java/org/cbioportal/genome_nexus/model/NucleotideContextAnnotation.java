
package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class NucleotideContextAnnotation
{

    @Field(value = "license")
    private String license;

    @Field(value = "annotation")
    private NucleotideContext annotation;

    public NucleotideContextAnnotation() { 
        this.annotation = new NucleotideContext();
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }

    public NucleotideContext getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(NucleotideContext annotation)
    {
        this.annotation = annotation;
    }

}